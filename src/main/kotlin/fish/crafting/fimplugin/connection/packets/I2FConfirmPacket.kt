package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionConstants
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import io.netty.buffer.ByteBufOutputStream

/**
 * Compatibility versions check. Runs right after the client sends the Init packet.
 * Could also be used as a sort of IJ -> MC Init.
 */
class I2FConfirmPacket(val compatible: Boolean) : OutPacket() {

    companion object{
        private val ID = PacketId("i2f_confirm")
    }

    override fun getId() = ID

    override fun write(stream: ByteBufOutputStream) {
        stream.writeBoolean(compatible)
        stream.writeInt(ConnectionConstants.COMPATIBILITY_VERSION)

        stream.writeLong(ProcessHandle.current().pid())
    }
}