package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import com.intellij.util.Alarm
import fish.crafting.fimplugin.plugin.listener.PluginDisposable
import fish.crafting.fimplugin.plugin.minimessage.parser.MiniMessageParser
import fish.crafting.fimplugin.plugin.util.DataKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class MiniMessageInlayController {

    var currentlyEditing: SmartPsiElementPointer<PsiElement>? = null
    private var blockInlay: Inlay<*>? = null

    companion object {
        fun removeAllFolds(removeInlays: Boolean = false) {
            val projects = ProjectManager.getInstance().openProjects
            for (project in projects) {
                val fileEditorManager = FileEditorManager.getInstance(project)
                for (fileEditor in fileEditorManager.allEditors) {
                    val textEditor = fileEditor as? TextEditor ?: continue
                    val editor = textEditor.editor
                    val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                    controller.removeFolds(editor)

                    if(removeInlays) {
                        controller.removeBlockInlay(editor, false)
                        controller.removeAllInlineInlays(editor)
                    }
                }
            }
        }
    }

    fun dispose(editor: Editor) {
        clearCaches()
        blockInlay?.dispose()
        blockInlay = null

        removeFolds(editor)
    }

    fun removeFolds(editor: Editor) {
        editor.foldingModel.runBatchFoldingOperation {
            editor.foldingModel.allFoldRegions.forEach {
                if(it.placeholderText.isEmpty()){
                    editor.foldingModel.removeFoldRegion(it)
                }
            }
        }
        (editor as? EditorEx)?.reinitSettings()
    }

    /**
     * If the user updates which methods are valid/invalid, this is all worthless now
     */
    fun clearCaches() {
        setCache(null)
    }

    fun setCache(expression: PsiElement?) {
        if(expression == null) {
            currentlyEditing = null
            return
        }

        currentlyEditing = SmartPointerManager.createPointer(expression)
    }

    fun matchesCached(expression: PsiElement): Boolean {
        val pointer = currentlyEditing ?: return false
        val element = pointer.element ?: return false
        return element == expression
    }

    fun getCachedExpr(): PsiElement? {
        val pointer = currentlyEditing ?: return null
        val element = pointer.element

        if(element == null) {
            clearCaches()
            return null
        }

        return element
    }

    fun getAndReplace(newExpression: PsiElement?): PsiElement? {
        val cachedExpr = getCachedExpr()
        setCache(newExpression)
        return cachedExpr
    }

    fun uncollapse(editor: Editor, expression: PsiElement) {
        val foldingModel = editor.foldingModel

        val startOffset = expression.startOffset
        val endOffset = expression.endOffset

        foldingModel.runBatchFoldingOperation {
            val region = foldingModel.getFoldRegion(startOffset, endOffset)
            if (region != null) {
                foldingModel.removeFoldRegion(region)
            }
        }
    }

    fun collapse(editor: Editor, expression: PsiElement) {
        val foldingModel = editor.foldingModel

        val startOffset = expression.startOffset
        val endOffset = expression.endOffset

        foldingModel.runBatchFoldingOperation {
            val existingRegion = foldingModel.getFoldRegion(startOffset, endOffset)

            if (existingRegion != null) {
                existingRegion.isExpanded = false
            } else {
                val region: FoldRegion? = foldingModel.addFoldRegion(
                    startOffset,
                    endOffset,
                    ""
                )

                if (region != null) {
                    region.isExpanded = false
                }
            }
        }
    }

    fun blockInlay(editor: Editor, expression: PsiElement, text: String){
        if(blockInlay != null){ //We don't need to check shouldOnlyRenderIfValid(), because if the user had at one point in this block inlay had tags, it should just stay
            if(matchesCached(expression)){ //The current inlay is correct, just update text.
                val renderer = blockInlay!!.renderer as MiniMessageRenderer
                val changed = renderer.updateText(text)

                if(changed) {
                    blockInlay!!.update()
                }

                //If the user is editing text and adds &k, try and add an update timer
                //This won't remove a timer if the user removes a &k, but that won't really be a problem
                //since there is only one block inlay at a time
                attachObfuscatedTimer(renderer, blockInlay)

                return
            }
        }

        //If there is no inlay, or it needs to update position, make a new one!
        val oldFocused = getAndReplace(expression)
        blockInlay?.dispose()

        removeInlineInlay(editor, expression)

        var appliedRenderer: MiniMessageRenderer? = null
        if(expression.shouldOnlyRenderIfValid()){
            val hadTags = AtomicBoolean(false)
            val parseOrLegacy = MiniMessageParser.parseOrLegacy(text, hadTags)

            if(hadTags.get()){
                appliedRenderer = MiniMessageRenderer(parseOrLegacy, text)
                blockInlay = editor.inlayModel.addBlockElement(
                    expression.endOffset,
                    false,
                    false,
                    100,
                    appliedRenderer
                )
            }else{
                blockInlay = null
            }
        }else{
            appliedRenderer = MiniMessageRenderer(text)
            blockInlay = editor.inlayModel.addBlockElement(
                expression.endOffset,
                false,
                false,
                100,
                appliedRenderer
            )
        }

        attachObfuscatedTimer(appliedRenderer, blockInlay)

        if(oldFocused != null){
            updateInlineFor(editor, oldFocused, false)
        }
    }

    @OptIn(FlowPreview::class)
    fun attachObfuscatedTimer(renderer: MiniMessageRenderer?, inlay: Inlay<*>?) {
        if(renderer != null && inlay != null && renderer.hasObfuscation()) {
            if(renderer.hasAttachedTimer) return
            renderer.attachTimer()

            //Register our inlay as disposable of our plugin so no memory leaks! :D
            Disposer.register(PluginDisposable.getInstance(), inlay)

            CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
                flow {
                    while (isActive) {
                        emit(Unit)
                        delay(50)
                    }
                }
                    .sample(50)
                    .flowOn(Dispatchers.Default)
                    .collect {
                        withContext(Dispatchers.EDT) {
                            inlay.update()
                        }
                    }
            }.also { scope ->
                Disposer.register(inlay) { scope.cancel() }
            }
        }
    }

    fun removeInlineInlay(editor: Editor, expression: PsiElement, uncollapse: Boolean = true) {
        val startOffset = expression.startOffset
        val endOffset = expression.endOffset

        if(uncollapse){
            uncollapse(editor, expression)
        }

        editor.inlayModel.getInlineElementsInRange(startOffset, endOffset)
            .filter { it.renderer is MiniMessageRenderer }
            .forEach { it.dispose() }
    }

    fun removeAllInlineInlays(editor: Editor){
        val document = editor.document

        editor.inlayModel.getInlineElementsInRange(0, document.textLength)
            .filter { it.renderer is MiniMessageRenderer }
            .forEach { it.dispose() }
    }

    fun removeBlockInlay(editor: Editor, inlineOld: Boolean = true){
        blockInlay?.dispose()
        blockInlay = null
        val oldFocused = getAndReplace(null)
        if(inlineOld && oldFocused != null){
            updateInlineFor(editor, oldFocused)
        }
    }

    fun refreshInlays(editor: Editor) {
        val project = editor.project ?: return
        val stringLiteral = getLiteralExpression(editor)
        stringLiteral ?: return

        stringLiteral.checkMCFormatAndRun(this) { result ->
            if (result) {
                PsiDocumentManager.getInstance(project).performLaterWhenAllCommitted {
                    val text = stringLiteral.getLiteralValue() as? String ?: ""

                    removeInlineInlay(editor, stringLiteral)
                    blockInlay(editor, stringLiteral, text)
                }
            }else{
                removeBlockInlay(editor)
            }
        }

    }
    /**
     * Even though this does return PsiElement, it will return null if the element is not a LiteralExpression.
     * The reason it returns PsiElement is for compatibility across languages/filetypes
     */
    private fun getLiteralExpression(editor: Editor): PsiElement? {
        if(editor.project == null) return null

        val caretOffset = editor.caretModel.offset
        val psiFile = PsiDocumentManager.getInstance(editor.project!!).getPsiFile(editor.document) ?: return null

        var psiElement = psiFile.findElementAt(caretOffset)
        var stringLiteral = psiElement?.getParentLiteral()

        if(stringLiteral == null){ //Try at offset - 1
            psiElement = psiFile.findElementAt(caretOffset - 1) ?: return null
            stringLiteral = psiElement.getParentLiteral()
        }

        return stringLiteral
    }

    private fun updateInlineStringLiteral(editor: Editor, text: String, expression: PsiElement){
        val offset = expression.textRange.endOffset
        val inlayModel = editor.inlayModel

        removeInlineInlay(editor, expression, false)

        var appliedRenderer: MiniMessageRenderer? = null
        var appliedInlay: Inlay<*>? = null

        if(expression.shouldOnlyRenderIfValid()){
            val hadTags = AtomicBoolean(false)
            val parseOrLegacy = MiniMessageParser.parseOrLegacy(text, hadTags)
            if(hadTags.get()){
                appliedRenderer = MiniMessageRenderer(parseOrLegacy, text)
                appliedInlay = inlayModel.addInlineElement(offset, true, appliedRenderer)
                collapse(editor, expression)
            }
        }else{
            appliedRenderer = MiniMessageRenderer(text)
            appliedInlay = inlayModel.addInlineElement(offset, true, appliedRenderer)
            collapse(editor, expression)
        }

        attachObfuscatedTimer(appliedRenderer, appliedInlay)
    }

    private fun handleCaretUpdate(editor: Editor) {
        val stringLiteral = getLiteralExpression(editor)

        if(stringLiteral == null) {
            removeBlockInlay(editor)
            return
        }

        refreshInlays(editor)
    }

    fun updateAllInlines(editor: Editor){
        val project = editor.project ?: return
        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return

        editor.inlayModel.getInlineElementsInRange(0, editor.document.textLength)
            .filter { it.renderer is MiniMessageRenderer }
            .forEach { it.dispose() }

        file.visitExpressions {
            updateInlineFor(editor, it)
        }
    }

    fun updateInlineFor(editor: Editor, expression: PsiElement, checkBlockInline: Boolean = true) {
        val value = expression.getLiteralValue()
        if(value !is String) return

        if(checkBlockInline && matchesCached(expression)) {
            blockInlay(editor, expression, value)
            return
        }

        expression.checkMCFormatAndRun(this) { result ->
            if(result){
                updateInlineStringLiteral(editor, value, expression)
            }else{
                removeInlineInlay(editor, expression)
            }
        }
    }

    fun handleDocumentChanged(editor: Editor) {
        refreshInlays(editor)
    }

    fun handleCaretChanged(editor: Editor) {
        handleCaretUpdate(editor)
    }
}