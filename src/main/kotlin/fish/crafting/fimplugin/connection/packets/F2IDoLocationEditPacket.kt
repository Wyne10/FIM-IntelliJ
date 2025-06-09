package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.packetsystem.InPacket
import fish.crafting.fimplugin.connection.tool.ValueEditManager
import fish.crafting.fimplugin.plugin.util.ReplaceUtil
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import io.netty.buffer.ByteBufInputStream
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.toUElement

object F2IDoLocationEditPacket : InPacket() {

    override fun readAndExecute(
        handler: MinecraftHandlerInstance,
        stream: ByteBufInputStream
    ) {
        val x = stream.readDouble()
        val y = stream.readDouble()
        val z = stream.readDouble()
        val pitch = stream.readFloat()
        val yaw = stream.readFloat()

        ValueEditManager.psiElement?.let {
            val uElement = it.toUElement()
            if(uElement is UCallExpression) {
                ReplaceUtil.modifyLocation(uElement, x, y, z, pitch, yaw, it.language.isKotlin)
            }
        }
    }
}