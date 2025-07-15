package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.netty.MinecraftHandlerInstance
import fish.crafting.fimplugin.connection.packetsystem.InPacket
import fish.crafting.fimplugin.connection.tool.ValueEditManager
import fish.crafting.fimplugin.plugin.util.ReplaceUtil
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import io.netty.buffer.ByteBufInputStream
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.toUElement

object F2IDoBoundingBoxEditPacket : InPacket() {

    override fun readAndExecute(
        handler: MinecraftHandlerInstance,
        stream: ByteBufInputStream
    ) {
        val x1 = stream.readDouble()
        val y1 = stream.readDouble()
        val z1 = stream.readDouble()
        val x2 = stream.readDouble()
        val y2 = stream.readDouble()
        val z2 = stream.readDouble()

        ValueEditManager.psiElement?.let {
            val uElement = it.toUElement()
            if(uElement is UQualifiedReferenceExpression){ //static .of()
                val expression = uElement.selector as? UCallExpression ?: return

                ReplaceUtil.modifyBoundingBox(expression, x1, y1, z1, x2, y2, z2, it.language.isKotlin)
            }else if(uElement is UCallExpression) {
                ReplaceUtil.modifyBoundingBox(uElement, x1, y1, z1, x2, y2, z2, it.language.isKotlin)
            }
        }
    }
}