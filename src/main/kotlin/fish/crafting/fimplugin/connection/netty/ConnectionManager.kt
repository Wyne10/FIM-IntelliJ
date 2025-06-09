package fish.crafting.fimplugin.connection.netty

import fish.crafting.fimplugin.connection.focuser.FocuserType
import fish.crafting.fimplugin.connection.focuser.ProgramFocuser
import fish.crafting.fimplugin.connection.focuser.WindowsProgramFocuser
import fish.crafting.fimplugin.connection.focuser.X11ProgramFocuser

object ConnectionManager {

    private var server: ConnectionServer? = null
    private var shutdown = false
    val ijFocuser = createFocuser()

    fun getServer(): ConnectionServer? {
        return if(server == null || shutdown) {
            null
        }else {
            server
        }
    }

    fun server(action: (ConnectionServer) -> Unit) {
        val s = getServer() ?: return
        action.invoke(s)
    }

    fun startServer() {
        if(server != null) return
        server = ConnectionServer()
        server!!.run()
    }

    fun shutdown() {
        shutdown = true
        server?.shutdown()
    }

    private fun createFocuser(): ProgramFocuser? {
        val current = FocuserType.getCurrent()
        return when(current) {
            FocuserType.WINDOWS -> WindowsProgramFocuser(ProcessHandle.current().pid().toInt())
            FocuserType.LINUX -> X11ProgramFocuser(ProcessHandle.current().pid().toInt())
            else -> null
        }
    }

    fun hasConnection(): Boolean {
        return server != null && server!!.hasInstance()
    }

}