package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.packetsystem.InPacket
import fish.crafting.fimplugin.connection.tool.ValueEditManager
import fish.crafting.fimplugin.plugin.util.ReplaceUtil
import io.netty.buffer.ByteBufInputStream
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.toUElement

object F2IMinecraftFocusedPacket : InPacket() {

    override fun readAndExecute(
        handler: MinecraftHandlerInstance,
        stream: ByteBufInputStream
    ) {
        ConnectionManager.server {
            it.markAsLatest(handler)
        }
    }
}