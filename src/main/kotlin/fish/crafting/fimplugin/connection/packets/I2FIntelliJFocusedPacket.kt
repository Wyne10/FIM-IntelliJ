package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionConstants
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import io.netty.buffer.ByteBufOutputStream

/**
 * Notifies minecraft that the user has opened/focused their IntelliJ.
 */
class I2FIntelliJFocusedPacket() : OutPacket() {

    companion object{
        private val ID = PacketId("i2f_ij_focused")
    }

    override fun getId() = ID

    override fun write(stream: ByteBufOutputStream) {
    }
}