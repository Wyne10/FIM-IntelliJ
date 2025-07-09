package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.util.ui.UIUtil
import fish.crafting.fimplugin.plugin.util.DataKeys
import org.jetbrains.kotlin.idea.gradleTooling.get

class ToggleMethodFormatAction : ToggleAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        val psi = e.getData(CommonDataKeys.PSI_ELEMENT)
        val presentation = e.presentation

        presentation.isEnabledAndVisible = psi is PsiMethod
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as? PsiMethod ?: return false
        return TextFormatRegistryService.instance.isMethodValid(method)
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as? PsiMethod ?: return
        TextFormatRegistryService.instance.markMethod(method, state)

        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return
        controller.updateAllInlines(editor)
    }

}