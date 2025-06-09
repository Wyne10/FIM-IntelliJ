package fish.crafting.fimplugin.connection.packetsystem

data class PacketId(val id: String) {
    fun compile() = id
}