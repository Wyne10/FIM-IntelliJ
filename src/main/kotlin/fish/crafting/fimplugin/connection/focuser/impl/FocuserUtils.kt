package fish.crafting.fimplugin.connection.focuser.impl

import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference

fun User32Impl.findProcessWindow(pid: Int, filter: ((WinDef.HWND) -> Boolean)): WinDef.HWND? {
    var winHandle: WinDef.HWND? = null

    EnumWindows(object : User32Impl.EnumThreadWindowsCallback {
        override fun callback(hWnd: WinDef.HWND?, lParam: IntByReference?): Boolean {
            if (hWnd == null) return true

            val pidReference = IntByReference()

            if (!GetWindowThreadProcessId(hWnd, pidReference)
                || pidReference.value != pid
                || !filter(hWnd)) return true


            winHandle = hWnd
            return false
        }
    }, null)

    return winHandle
}