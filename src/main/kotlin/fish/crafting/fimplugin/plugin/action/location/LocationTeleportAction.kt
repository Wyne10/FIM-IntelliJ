package fish.crafting.fimplugin.plugin.action.location

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiNewExpression
import fish.crafting.fimplugin.plugin.action.ConstructorBasedAction
import fish.crafting.fimplugin.plugin.util.ActionUtils
import org.jetbrains.kotlin.psi.KtCallExpression

class LocationTeleportAction : ConstructorBasedAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun performAction(
        e: AnActionEvent,
        newExpression: PsiNewExpression
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