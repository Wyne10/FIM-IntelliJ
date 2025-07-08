package fish.crafting.fimplugin.plugin.minimessage

import ai.grazie.utils.attributes.value
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.Inlay
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import com.intellij.util.concurrency.AppExecutorUtil
import kotlin.math.exp

class MiniMessageInlayController {
    private var blockInlay: Inlay<*>? = null
    private var focusedExpression: PsiLiteralExpression? = null

    fun dispose() {
        focusedExpression = null
        blockInlay?.dispose()
        blockInlay = null
    }

    fun uncollapse(editor: Editor, expression: PsiLiteralExpression) {
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

    fun collapse(editor: Editor, expression: PsiLiteralExpression) {
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

    fun blockInlay(editor: Editor, expression: PsiLiteralExpression, text: String){
        if(blockInlay != null){
            if(focusedExpression == expression){ //The current inlay is correct, just update text.
                val renderer = blockInlay!!.renderer as MiniMessageRenderer
                renderer.updateText(text)
                return
            }
        }

        //If there is no inlay, or it needs to update position, make a new one!
        val oldFocused = focusedExpression
        focusedExpression = expression
        blockInlay?.dispose()

        removeInlineInlay(editor, expression)

        blockInlay = editor.inlayModel.addBlockElement(
            expression.endOffset,
            false,
            false,
            100,
            MiniMessageRenderer(text)
        )

        if(oldFocused != null){
            updateInlineFor(editor, oldFocused)
        }
    }

    fun removeInlineInlay(editor: Editor, expression: PsiLiteralExpression) {
        val startOffset = expression.startOffset
        val endOffset = expression.endOffset

        uncollapse(editor, expression)

        editor.inlayModel.getInlineElementsInRange(startOffset, endOffset)
            .filter { it.renderer is MiniMessageRenderer }
            .forEach { it.dispose() }
    }

    fun removeBlockInlay(editor: Editor){
        blockInlay?.dispose()
        blockInlay = null
        val oldFocused = focusedExpression
        focusedExpression = null
        if(oldFocused != null){
            updateInlineFor(editor, oldFocused)
        }
    }

    fun refreshInlays(editor: Editor) {
        val project = editor.project ?: return
        val stringLiteral = getLiteralExpression(editor) ?: return

        ReadAction.nonBlocking<Boolean> {
            stringLiteral.shouldFormatMCText()
        }.finishOnUiThread(ApplicationManager.getApplication().defaultModalityState) { result ->
            if (result) {
                PsiDocumentManager.getInstance(project).performLaterWhenAllCommitted {
                    val text = stringLiteral.value as? String ?: ""

                    removeInlineInlay(editor, stringLiteral)
                    blockInlay(editor, stringLiteral, text)
                }
            }else{
                removeBlockInlay(editor)
            }
        }.submit(AppExecutorUtil.getAppExecutorService())

    }

    private fun getLiteralExpression(editor: Editor): PsiLiteralExpression? {
        if(editor.project == null) return null

        val caretOffset = editor.caretModel.offset
        val psiFile = PsiDocumentManager.getInstance(editor.project!!).getPsiFile(editor.document) ?: return null

        var psiElement = psiFile.findElementAt(caretOffset) ?: return null
        var stringLiteral = PsiTreeUtil.getParentOfType(psiElement, PsiLiteralExpression::class.java)

        if(stringLiteral == null){ //Try at offset - 1
            psiElement = psiFile.findElementAt(caretOffset - 1) ?: return null
            stringLiteral = PsiTreeUtil.getParentOfType(psiElement, PsiLiteralExpression::class.java)
        }

        return stringLiteral
    }
    private fun updateInlineStringLiteral(editor: Editor, text: String, expression: PsiLiteralExpression){
        val offset = expression.textRange.endOffset
        val inlayModel = editor.inlayModel

        inlayModel.addInlineElement(offset, true, MiniMessageRenderer(text))
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

        file.accept(object : JavaRecursiveElementVisitor() {
            override fun visitLiteralExpression(expression: PsiLiteralExpression) {
                updateInlineFor(editor, expression)
            }
        })
    }

    private fun updateInlineFor(editor: Editor, expression: PsiLiteralExpression) {
        if(expression == focusedExpression) {
            val value = expression.value
            if(value is String){
                blockInlay(editor, expression, value)
                return
            }
        }

        val value = expression.value
        if(value !is String) return

        ReadAction.nonBlocking<Boolean> {
            expression.shouldFormatMCText()
        }.finishOnUiThread(ApplicationManager.getApplication().defaultModalityState) { result ->
            if(result){
                collapse(editor, expression)
                updateInlineStringLiteral(editor, value, expression)
            }else{
                removeInlineInlay(editor, expression)
            }
        }.submit(AppExecutorUtil.getAppExecutorService())
    }

    fun handleDocumentChanged(editor: Editor) {
        refreshInlays(editor)
    }

    fun handleCaretChanged(editor: Editor) {
        handleCaretUpdate(editor)
    }
}