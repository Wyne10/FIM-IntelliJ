package fish.crafting.fimplugin.connection.focuser

import com.intellij.ui.User32Ex
import com.intellij.util.findProcessWindow
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import javax.print.DocFlavor

class WindowsProgramFocuser(val pid: Int) : ProgramFocuser {

    private var hWnd: WinDef.HWND? = getHwnd(pid)

    override fun focus(): Boolean {
        if(hWnd == null) return false
        User32Ex.INSTANCE.SetForegroundWindow(hWnd)

        return true
    }

    private fun getHwnd(pid: Int): WinDef.HWND? {
        return User32Ex.INSTANCE.findProcessWindow(pid.toUInt()) {
            /*
             * There are a lot of processes that the PID returns, and sometimes the first one is not the Minecraft window.
             * However, only the Minecraft window is visible, so only filter that one
             */
            User32Ex.INSTANCE.IsWindowVisible(it)
        }
    }
}