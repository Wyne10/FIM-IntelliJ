package fish.crafting.fimplugin.plugin.action.boundingbox

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiCallExpression
import fish.crafting.fimplugin.plugin.action.ConstructorBasedAction
import fish.crafting.fimplugin.plugin.util.ActionUtils
import org.jetbrains.kotlin.psi.KtCallExpression

class BoundingBoxTeleportAction : ConstructorBasedAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun performAction(
        e: AnActionEvent,
        newExpression: PsiCallExpression
    ) {
        ActionUtils.tpLocation(newExpression, e.inputEvent)
    }

    override fun ktPerformAction(
        e: AnActionEvent,
        callExpression: KtCallExpression
    ) {
        ActionUtils.tpLocation(callExpression, e.inputEvent)
    }
}