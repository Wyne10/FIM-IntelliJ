package fish.crafting.fimplugin.plugin.action.location

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiCallExpression
import fish.crafting.fimplugin.connection.focuser.ProgramFocuser
import fish.crafting.fimplugin.connection.packets.I2FEditPacket
import fish.crafting.fimplugin.connection.packets.I2FTeleportPacket
import fish.crafting.fimplugin.connection.tool.MinecraftEditorTool
import fish.crafting.fimplugin.connection.tool.ValueEditManager
import fish.crafting.fimplugin.plugin.action.ConstructorBasedAction
import fish.crafting.fimplugin.plugin.util.ifLocation
import org.jetbrains.kotlin.psi.KtCallExpression

class LocationMoveAction : ConstructorBasedAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun performAction(
        e: AnActionEvent,
        newExpression: PsiCallExpression
    ) {
        newExpression.ifLocation {
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
        callExpression.ifLocation {
            ValueEditManager.psiElement = callExpression
            I2FEditPacket(it, MinecraftEditorTool.MOVE).sendToLatest()
            I2FTeleportPacket(it).sendToLatest()
            ProgramFocuser.focusLatest()
        }
    }
}
