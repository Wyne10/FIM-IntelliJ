package fish.crafting.fimplugin.connection.packetsystem

import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import io.netty.buffer.ByteBufInputStream
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

abstract class InPacket {

    abstract fun readAndExecute(handler: MinecraftHandlerInstance, stream: ByteBufInputStream)

    fun readUUID(stream: ByteBufInputStream): UUID = UUID.fromString(stream.readUTF())
}