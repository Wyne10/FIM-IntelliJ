package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.packetsystem.InPacket
import io.netty.buffer.ByteBufInputStream

object F2IInitPacket : InPacket() {

    override fun readAndExecute(
        handler: MinecraftHandlerInstance,
        stream: ByteBufInputStream
    ) {
        val uuid = readUUID(stream)
        val compatibilityVersion = stream.readInt()

        val focuser = FocuserType.readFromStream(stream)

        handler.init(uuid, compatibilityVersion, focuser)
    }
}