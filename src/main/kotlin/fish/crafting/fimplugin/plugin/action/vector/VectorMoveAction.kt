package fish.crafting.fimplugin.plugin.action.vector

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNewExpression
import fish.crafting.fimplugin.connection.focuser.ProgramFocuser
import fish.crafting.fimplugin.connection.packets.I2FEditPacket
import fish.crafting.fimplugin.connection.packets.I2FTeleportPacket
import fish.crafting.fimplugin.connection.tool.MinecraftEditorTool
import fish.crafting.fimplugin.connection.tool.ValueEditManager
import fish.crafting.fimplugin.plugin.action.ConstructorBasedAction
import fish.crafting.fimplugin.plugin.util.ifVector
import org.jetbrains.kotlin.psi.KtCallExpression

class VectorMoveAction : ConstructorBasedAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun performAction(
        e: AnActionEvent,
        newExpression: PsiNewExpression
    ) {
        newExpression.ifVector {
            ValueEditManager.psiElement = newExpression
            I2FEditPacket(it, MinecraftEditorTool.MOVE).sendToLatest()
            I2FTeleportPacket(it).sendToLatest()
            ProgramFocuser.focusLatest()
        }
    }

    override fun ktPerformAction(
        e: AnActionEvent,
        callExpression: KtCallExpression
    ) {
        callExpression.ifVector {
            ValueEditManager.psiElement = callExpression
            I2FEditPacket(it, MinecraftEditorTool.MOVE).sendToLatest()
            I2FTeleportPacket(it).sendToLatest()
            ProgramFocuser.focusLatest()
        }
    }
}
