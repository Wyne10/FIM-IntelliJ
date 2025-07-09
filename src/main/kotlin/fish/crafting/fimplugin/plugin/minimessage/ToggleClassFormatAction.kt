package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.util.ui.UIUtil
import fish.crafting.fimplugin.plugin.util.DataKeys
import org.jetbrains.kotlin.idea.gradleTooling.get
import org.jetbrains.plugins.terminal.block.util.TerminalDataContextUtils.editor

class ToggleClassFormatAction : ToggleAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        val psi = e.getData(CommonDataKeys.PSI_ELEMENT)
        val presentation = e.presentation

        presentation.isEnabledAndVisible = psi is PsiClass
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as? PsiClass ?: return false
        return TextFormatRegistryService.instance.isClassValid(method)
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as? PsiClass ?: return
        TextFormatRegistryService.instance.markClass(method, state)

        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return
        controller.updateAllInlines(editor)

    }

}