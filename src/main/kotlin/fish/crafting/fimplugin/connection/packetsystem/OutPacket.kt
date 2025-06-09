package fish.crafting.fimplugin.connection.packetsystem

import fish.crafting.fimplugin.connection.netty.MinecraftAudience
import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.netty.MinecraftManager
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled

abstract class OutPacket {

    protected abstract fun getId(): PacketId
    protected abstract fun write(stream: ByteBufOutputStream)

    fun send(handler: MinecraftHandlerInstance) {
        writeAndSend {
            handler.send(it)
        }
    }

    fun sendToLatest() = send(MinecraftAudience.LATEST)
    fun sendToAll() = send(MinecraftAudience.ALL)

    fun send(audience: MinecraftAudience) {
        writeAndSend {
            if(audience == MinecraftAudience.LATEST) {
                MinecraftManager.sendToLatestInstance(it)
            }else if(audience == MinecraftAudience.ALL) {
                MinecraftManager.sendToAllInstances(it)
            }
        }
    }

    private fun writeAndSend(unit: (ByteBuf) -> Unit) {
        val buffer = Unpooled.buffer()
        val stream = ByteBufOutputStream(buffer)

        stream.use {
            it.writeUTF(getId().compile()) //Identifier

            write(it)
            unit.invoke(buffer)
        }
    }

}