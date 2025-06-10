package fish.crafting.fimplugin.connection.focuser.impl

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions
import javax.security.auth.callback.Callback

interface KtUser32Impl : StdCallLibrary {
    companion object{
        val INSTANCE: KtUser32Impl = Native.load("user32", KtUser32Impl::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun SetForegroundWindow(hwnd: WinDef.HWND?): Boolean

    fun IsWindowVisible(hwnd: WinDef.HWND?): Boolean

    fun EnumWindows(callback: EnumThreadWindowsCallback, extraData: WinDef.INT_PTR?): Boolean

    fun GetWindowThreadProcessId(handle: WinDef.HWND?, lpdwProcessId: IntByReference?): Boolean

    interface EnumThreadWindowsCallback : Callback {
        fun callback(hWnd: WinDef.HWND?, lParam: IntByReference?): Boolean
    }

}