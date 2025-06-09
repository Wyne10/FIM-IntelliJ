package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.netty.ConnectionConstants
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import io.netty.buffer.ByteBufOutputStream

class I2FFocusPacket() : OutPacket() {

    companion object{
        private val ID = PacketId("i2f_focus")
    }

    override fun getId() = ID

    override fun write(stream: ByteBufOutputStream) {
    }
}