package fish.crafting.fimplugin.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiCallExpression
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.plugin.util.DataKeys
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Actions that require a constructor to run on.
 * Example: Teleporting to a Vector(1.0, 1.0, 1.0)
 */
abstract class ConstructorBasedAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.dataContext.getData(DataKeys.PASSED_ELEMENT) ?: return

        if(element.language.isKotlin){
            val callExpr = element as? KtCallExpression
            if(callExpr == null) return

            ktPerformAction(e, callExpr)
        }else{
            val newExpr = element as? PsiCallExpression
            if(newExpr == null) return

            performAction(e, newExpr)
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if(project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        e.presentation.isVisible = true
        e.presentation.isEnabled = ConnectionManager.hasConnection()
    }

    protected abstract fun performAction(e: AnActionEvent, newExpression: PsiCallExpression)
    protected abstract fun ktPerformAction(e: AnActionEvent, callExpression: KtCallExpression)
}