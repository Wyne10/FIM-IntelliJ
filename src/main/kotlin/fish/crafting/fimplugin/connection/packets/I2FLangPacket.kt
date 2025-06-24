package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionConstants
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import io.netty.buffer.ByteBufOutputStream

/**
 * Tells Minecraft which language to use when copying values like vectors
 */
class I2FLangPacket(val kotlin: Boolean) : OutPacket() {

    companion object{
        private val ID = PacketId("i2f_lang")
    }

    override fun getId() = ID

    override fun write(stream: ByteBufOutputStream) {
        stream.writeBoolean(kotlin)
    }
}