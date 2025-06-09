package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.netty.ConnectionConstants
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import fish.crafting.fimplugin.connection.tool.MinecraftEditorTool
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import fish.crafting.fimplugin.plugin.util.writeLocation
import fish.crafting.fimplugin.plugin.util.writeVector
import io.netty.buffer.ByteBufOutputStream

/**
 * Tells minecraft to start editing a certain value.
 */
class I2FEditPacket private constructor(val writer: (ByteBufOutputStream) -> Unit, val tool: MinecraftEditorTool? = null) : OutPacket() {

    companion object{
        private val ID = PacketId("i2f_edit")
    }

    constructor(vector: Vector, tool: MinecraftEditorTool? = null) : this({
        it.writeVector(vector, true)
    }, tool)

    constructor(location: Location, tool: MinecraftEditorTool? = null) : this({
        it.writeLocation(location, true)
    }, tool)

    override fun getId() = ID

    override fun write(stream: ByteBufOutputStream) {
        writer.invoke(stream)
        stream.writeInt(tool?.id ?: -1)
    }
}