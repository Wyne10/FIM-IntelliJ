package fish.crafting.fimplugin.connection.focuser

import fish.crafting.focuser.X11Impl

class X11ProgramFocuser(val pid: Int) : ProgramFocuser {
    override fun focus(): Boolean {
        val mainWindow = X11Impl.findWindowByPid(pid) ?: run {
            return false
        }

        return X11Impl.focus(mainWindow)
    }
}