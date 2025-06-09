package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.packetsystem.InPacket
import io.netty.buffer.ByteBufInputStream

object F2IFocusPacket : InPacket() {

    override fun readAndExecute(
        handler: MinecraftHandlerInstance,
        stream: ByteBufInputStream
    ) {
        ConnectionManager.ijFocuser?.focus()
    }
}