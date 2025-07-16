package fish.crafting.fimplugin.plugin.action.inlay

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Inlay
import com.intellij.psi.PsiClass
import fish.crafting.fimplugin.plugin.minimessage.MiniMessageRenderer
import fish.crafting.fimplugin.plugin.util.DataKeys

class RemoveInlayAction : AnAction() {
    override fun update(e: AnActionEvent) {
        super.update(e)

        val presentation = e.presentation
        presentation.isEnabledAndVisible = enabled(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun enabled(e: AnActionEvent): Boolean {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return false
        val caret = editor.caretModel.currentCaret
        val offset = caret.offset

        val inlays = editor.inlayModel.getInlineElementsInRange(offset, offset)
        return inlays.isNotEmpty() && inlays.any { it.isOurs() }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

        val caret = editor.caretModel.currentCaret
        val offset = caret.offset
        val inlays = editor.inlayModel.getInlineElementsInRange(offset - 1, offset + 1)

        for (inlay in inlays) {
            if(inlay.isOurs()) {
                controller.removeInlayAndFold(editor, inlay)
            }
        }
    }
}
fun Inlay<*>.isOurs(): Boolean{
    return isValid && renderer is MiniMessageRenderer
}