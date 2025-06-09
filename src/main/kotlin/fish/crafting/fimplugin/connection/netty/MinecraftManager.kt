package fish.crafting.fimplugin.connection.netty

import io.netty.buffer.ByteBuf

object MinecraftManager {

    fun sendToLatestInstance(buffer: ByteBuf) {
        val server = ConnectionManager.getServer() ?: return
        server.getLatestInstance()?.send(buffer)
    }

    fun sendToAllInstances(buffer: ByteBuf) {
        val server = ConnectionManager.getServer() ?: return
        server.sendToAllConnections(buffer)
    }

    fun getLatestInstance(): MinecraftHandlerInstance? {
        val server = ConnectionManager.getServer() ?: return null
        return server.getLatestInstance()
    }

}