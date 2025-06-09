package fish.crafting.fimplugin.connection.focuser

import com.intellij.openapi.components.service
import com.intellij.openapi.wm.impl.X11UiUtil

//Copied from IntelliJ's X11BringProcessWindowToForegroundSupport
class X11ProgramFocuser(val pid: Int) : ProgramFocuser {
    override fun focus(): Boolean {
        val mainWindow = X11UiUtil.findVisibleWindowByPid(pid.toLong()) ?: run {
            return false
        }
        return X11UiUtil.activate(mainWindow)
    }
}