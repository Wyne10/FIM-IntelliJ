package fish.crafting.fimplugin.connection.focuser.impl;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.callback.Callback;

public interface User32Impl extends StdCallLibrary {
    User32Impl INSTANCE = Native.load("user32", User32Impl.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean SetForegroundWindow(WinDef.HWND hwnd);

    boolean IsWindowVisible(com.sun.jna.platform.win32.WinDef.HWND hwnd);

    boolean EnumWindows(@NotNull EnumThreadWindowsCallback callback, @Nullable WinDef.INT_PTR extraData);

    boolean GetWindowThreadProcessId(WinDef.HWND handle, IntByReference lpdwProcessId);

    interface EnumThreadWindowsCallback extends Callback {
        boolean callback(WinDef.HWND hWnd, IntByReference lParam);
    }
}
