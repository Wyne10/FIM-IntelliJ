package fish.crafting.fimplugin.plugin.util

import fish.crafting.fimplugin.connection.tool.MinecraftEditorTool
import fish.crafting.fimplugin.plugin.util.mc.BoundingBox
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream

/*
 GUIDE FOR WILDCARDS
 0 - Vector
 1 - Location
 2 - Bounding Box
*/

fun ByteBufOutputStream.writeVector(vector: Vector, writeAsWildcard: Boolean = false) {
    if(writeAsWildcard) writeInt(0)
    writeDouble(vector.x)
    writeDouble(vector.y)
    writeDouble(vector.z)
}

fun ByteBufOutputStream.writeLocation(location: Location, writeAsWildcard: Boolean = false) {
    if(writeAsWildcard) writeInt(1)
    writeDouble(location.x)
    writeDouble(location.y)
    writeDouble(location.z)
    writeFloat(location.pitch)
    writeFloat(location.yaw)
    writeUTF(location.world)
}

fun ByteBufOutputStream.writeBoundingBox(boundingBox: BoundingBox, writeAsWildcard: Boolean = false) {
    if(writeAsWildcard) writeInt(2)
    writeDouble(boundingBox.x1)
    writeDouble(boundingBox.y1)
    writeDouble(boundingBox.z1)
    writeDouble(boundingBox.x2)
    writeDouble(boundingBox.y2)
    writeDouble(boundingBox.z2)
}

fun ByteBufInputStream.readVector(): Vector {
    return Vector(
        readDouble(),
        readDouble(),
        readDouble()
    )
}

fun ByteBufOutputStream.writeTool(tool: MinecraftEditorTool) {
    writeInt(tool.id)
}