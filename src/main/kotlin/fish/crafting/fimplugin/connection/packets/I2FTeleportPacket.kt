package fish.crafting.fimplugin.connection.packets

import fish.crafting.fimplugin.connection.packetsystem.InPacket
import fish.crafting.fimplugin.connection.packetsystem.OutPacket
import fish.crafting.fimplugin.connection.packetsystem.PacketId
import fish.crafting.fimplugin.plugin.util.mc.BoundingBox
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import io.netty.buffer.ByteBufOutputStream
import org.jetbrains.kotlin.idea.core.util.writeString

class I2FTeleportPacket(val x: Double,
                        val y: Double,
                        val z: Double,
                        val pitch: Float? = null,
                        val yaw: Float? = null,
                        val world: String? = null): OutPacket() {

    constructor(vector: Vector) : this(vector.x, vector.y, vector.z)
    constructor(location: Location) : this(location.x, location.y, location.z, location.pitch, location.yaw, location.world)
    constructor(box: BoundingBox) : this(box.centerX, box.centerY, box.centerZ)

    companion object {
        private val ID = PacketId("i2f_tp")
    }

    override fun getId() = ID

    /*

    DOUBLE - x
    DOUBLE - y
    DOUBLE - z
    BOOLEAN - Has Rotation
     IF TRUE:
       | FLOAT - Pitch
       | FLOAT - Yaw
    STRING - world, empty string if null

     */
    override fun write(stream: ByteBufOutputStream) {
        stream.writeDouble(x)
        stream.writeDouble(y)
        stream.writeDouble(z)

        if(pitch != null && yaw != null) {
            stream.writeBoolean(true) //Has rotation

            stream.writeFloat(pitch)
            stream.writeFloat(yaw)
        }else{
            stream.writeBoolean(false)
        }

        stream.writeUTF(world ?: "")
    }

}