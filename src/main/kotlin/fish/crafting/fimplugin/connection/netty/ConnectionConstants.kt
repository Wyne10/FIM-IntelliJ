package fish.crafting.fimplugin.connection.netty

object ConnectionConstants {

    const val CONNECTION_PORT = 1101
    const val NOT_INITIALIZED_TIMEOUT = 10_000L

    //If the plugin and the mod mismatch these, they can't work together.
    const val COMPATIBILITY_VERSION = 3
}