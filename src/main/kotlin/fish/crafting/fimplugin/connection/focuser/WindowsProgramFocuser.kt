package fish.crafting.fimplugin.connection.focuser

import com.intellij.util.findProcessWindow
import com.sun.jna.platform.win32.WinDef
import fish.crafting.fimplugin.connection.focuser.impl.KtUser32Impl
import fish.crafting.fimplugin.connection.focuser.impl.findProcessWindow

class WindowsProgramFocuser(pid: Int) : ProgramFocuser {

    private var hWnd: WinDef.HWND? = getHwnd(pid)

    override fun focus(): Boolean {
        if(hWnd == null) return false
        KtUser32Impl.INSTANCE.SetForegroundWindow(hWnd)

        return true
    }

    private fun getHwnd(pid: Int): WinDef.HWND? {
        return KtUser32Impl.INSTANCE.findProcessWindow(pid) {
            /*
             * There are a lot of processes that the PID returns, and sometimes the first one is not the Minecraft window.
             * However, only the Minecraft window is visible, so only filter that one
             */
            KtUser32Impl.INSTANCE.IsWindowVisible(it)
        }
    }
}