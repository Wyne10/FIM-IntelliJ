package fish.crafting.focuser;

import com.intellij.openapi.Disposable;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.ui.StartupUiUtil;
import com.sun.jna.Native;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static com.intellij.util.ReflectionUtil.getStaticFieldValue;

public class X11Impl {

    private static final Object unsafe;

    private static final long ANY_PROPERTY_TYPE = 0;
    private static final int CLIENT_MESSAGE = 33;
    private static final int FORMAT_BYTE = 8;
    private static final int FORMAT_LONG = 32;
    private static final long EVENT_MASK = (3L << 19);

    private static final Xlib X11 = Xlib.getInstance();

    static {
        Class<?> unsafeClass;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        unsafe = getStaticFieldValue(unsafeClass, unsafeClass, "theUnsafe");
        if (unsafe == null) {
            throw new RuntimeException("Could not find 'theUnsafe' field in the Unsafe class");
        }
    }

    private static final class Xlib {
        private Unsafe unsafe;
        private Method XGetWindowProperty;
        private Method XFree;
        private Method RootWindow;
        private Method XSendEvent;
        private Method awtLock;
        private Method awtUnlock;
        private Method getChildWindows;
        private Method XGetWindowAttributes;

        private long display;

        private long NET_ACTIVE_WINDOW;
        private long _NET_WM_PID;

        private static @Nullable Xlib getInstance() {
            Class<? extends Toolkit> toolkitClass = Toolkit.getDefaultToolkit().getClass();
            if (!StartupUiUtil.isXToolkit()) return null;

            try {
                Xlib x11 = new Xlib();

                Class<?> XlibWrapper = Class.forName("sun.awt.X11.XlibWrapper");
                x11.unsafe = (Unsafe) X11Impl.unsafe;
                x11.XGetWindowProperty = method(XlibWrapper, "XGetWindowProperty", 12);
                x11.XFree = method(XlibWrapper, "XFree", 1);
                x11.RootWindow = method(XlibWrapper, "RootWindow", 2);
                x11.XSendEvent = method(XlibWrapper, "XSendEvent", 5);
                x11.XGetWindowAttributes = method(XlibWrapper, "XGetWindowAttributes", 3);
                x11.display = (Long)method(toolkitClass, "getDisplay").invoke(null);
                x11.awtLock = method(toolkitClass, "awtLock");
                x11.awtUnlock = method(toolkitClass, "awtUnlock");

                Class<?> XlibUtil = Class.forName("sun.awt.X11.XlibUtil");
                x11.getChildWindows = method(XlibUtil, "getChildWindows", long.class);

                Class<?> XAtom = Class.forName("sun.awt.X11.XAtom");
                Method get = method(XAtom, "get", String.class);
                Field atom = field(XAtom, "atom");
                x11.NET_ACTIVE_WINDOW = (Long)atom.get(get.invoke(null, "_NET_ACTIVE_WINDOW"));
                x11._NET_WM_PID = (Long)atom.get(get.invoke(null, "_NET_WM_PID"));

                return x11;
            } catch (Exception ignored) {}

            return null;
        }

        private long getRootWindow(long screen) throws Exception {
            awtLock.invoke(null);
            try {
                return (Long)RootWindow.invoke(null, display, screen);
            }
            finally {
                awtUnlock.invoke(null);
            }
        }

        private @Nullable <T> T getWindowProperty(long window, long name, long type, long expectedFormat) throws Exception {
            long data = unsafe.allocateMemory(64);
            awtLock.invoke(null);
            try {
                unsafe.setMemory(data, 64, (byte)0);

                int result = (Integer)XGetWindowProperty.invoke(
                        null, display, window, name, 0L, 65535L, 0L, type, data, data + 8, data + 16, data + 24, data + 32);
                if (result == 0) {
                    int format = unsafe.getInt(data + 8);
                    long pointer = Native.LONG_SIZE == 4 ? unsafe.getInt(data + 32) : unsafe.getLong(data + 32);

                    if (pointer != 0 && format == expectedFormat) {
                        int length = Native.LONG_SIZE == 4 ? unsafe.getInt(data + 16) : (int)unsafe.getLong(data + 16);
                        if (format == FORMAT_BYTE) {
                            byte[] bytes = new byte[length];
                            for (int i = 0; i < length; i++) bytes[i] = unsafe.getByte(pointer + i);
                            return (T)bytes;
                        }
                        else if (format == FORMAT_LONG) {
                            long[] values = new long[length];
                            for (int i = 0; i < length; i++) {
                                values[i] = Native.LONG_SIZE == 4 ? unsafe.getInt(pointer + 4L * i) : unsafe.getLong(pointer + 8L * i);
                            }
                            return (T)values;
                        }
                    }

                    if (pointer != 0) XFree.invoke(null, pointer);

                }
            } finally {
                awtUnlock.invoke(null);
                unsafe.freeMemory(data);
            }

            return null;
        }

        private void sendClientMessage(long target, long window, long type, long... data) throws Exception {
            assert data.length <= 5;
            long event = unsafe.allocateMemory(128);
            awtLock.invoke(null);
            try {
                unsafe.setMemory(event, 128, (byte)0);

                unsafe.putInt(event, CLIENT_MESSAGE);
                if (Native.LONG_SIZE == 4) {
                    unsafe.putInt(event + 8, 1);
                    unsafe.putInt(event + 16, (int)window);
                    unsafe.putInt(event + 20, (int)type);
                    unsafe.putInt(event + 24, FORMAT_LONG);
                    for (int i = 0; i < data.length; i++) {
                        unsafe.putInt(event + 28 + 4L * i, (int)data[i]);
                    }
                }
                else {
                    unsafe.putInt(event + 16, 1);
                    unsafe.putLong(event + 32, window);
                    unsafe.putLong(event + 40, type);
                    unsafe.putInt(event + 48, FORMAT_LONG);
                    for (int i = 0; i < data.length; i++) {
                        unsafe.putLong(event + 56 + 8L * i, data[i]);
                    }
                }

                XSendEvent.invoke(null, display, target, false, EVENT_MASK, event);
            }
            finally {
                awtUnlock.invoke(null);
                unsafe.freeMemory(event);
            }
        }


        private Long findProcessWindow(long window, long pid, int depth) throws InvocationTargetException, IllegalAccessException {
            if(depth > 30) return null;

            if (isProcessWindowOwner(window, pid) && isViewableWin(window)) {
                return window;
            }

            Set<Long> children = getChildWindows(window);
            if (children == null) return null;

            for (long child : children) {
                var childWindow = findProcessWindow(child, pid, depth + 1);
                if (childWindow != null) {
                    return childWindow;
                }
            }

            return null;
        }

        private static Boolean isViewableWin(long window) throws InvocationTargetException, IllegalAccessException {
            assert X11 != null;
            XWindowAttributesWrapper wrapper = null;
            try {
                X11.awtLock.invoke(null);
                wrapper = new XWindowAttributesWrapper(window);
                return wrapper.getMapState() == XWindowAttributesWrapper.MapState.IsViewable;
            } catch (Exception ignored) {
                return false;
            } finally {
                X11.awtUnlock.invoke(null);
                if (wrapper != null)
                    wrapper.dispose();
            }
        }

        private boolean isProcessWindowOwner(Long window, long pid) {
            long[] value;
            try {
                value = getWindowProperty(window, _NET_WM_PID, ANY_PROPERTY_TYPE, FORMAT_LONG);
            } catch (Exception ignored) {
                return false;
            }

            if (value == null) return false;
            return value[0] == pid;
        }

        public Set<Long> getChildWindows(Long window) {
            try {
                return (Set<Long>) getChildWindows.invoke(null, window);
            } catch (Exception ignored) {}
            return null;
        }
    }

    private static Field field(Class<?> aClass, @NonNls String name) throws Exception {
        Field field = aClass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static Method method(Class<?> aClass, @NonNls String name, Class<?>... parameterTypes) throws Exception {
        while (aClass != null) {
            try {
                Method method = aClass.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method;
            }
            catch (NoSuchMethodException e) {
                aClass = aClass.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name);
    }

    private static Method method(Class<?> aClass, @NonNls String name, int parameters) throws Exception {
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.getParameterCount() == parameters && name.equals(method.getName())) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(name);
    }

    public static Long findWindowByPid(int pid) {
        if (X11 == null) return null;

        try {
            var rootWindow = X11.getRootWindow(0);
            return X11.findProcessWindow(rootWindow, pid, 0);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean focus(long windowId) {
        if (X11 == null) return false;

        try {
            var window = X11.getRootWindow(0);
            X11.sendClientMessage(window, windowId, X11.NET_ACTIVE_WINDOW);
            return true;
        } catch (Exception ignored) {}


        return false;
    }

    static final class XWindowAttributesWrapper implements Disposable {
        enum MapState {
            IsUnmapped,
            IsUnviewable,
            IsViewable,
        }

        MapState getMapState() throws InvocationTargetException, IllegalAccessException {
            return MapState.values()[(Integer)get_map_state.invoke(instance)];
        }

        XWindowAttributesWrapper(long windowId) throws Exception {
            assert X11 != null;

            instance = getXWindowAttributesConstructor().newInstance();
            var ptrData = getPData.invoke(instance);

            var operationResult = (Integer)X11.XGetWindowAttributes.invoke(null, X11.display, windowId, ptrData);
            if (operationResult == 0) {
                throw new Exception("XGetWindowAttributes failed :" + operationResult);
            }
        }

        @Override
        public void dispose() {
            try {
                dispose.invoke(instance);
            } catch (Exception ignored) {
            }
        }

        private final Object instance;

        private static Constructor<?> constructor;
        private static Method get_map_state;
        private static Method getPData;
        private static Method dispose;

        private static Constructor<?> getXWindowAttributesConstructor() throws Exception {
            if (constructor == null) {
                var clazz = Class.forName("sun.awt.X11.XWindowAttributes");
                constructor = clazz.getConstructor();
                get_map_state = method(clazz, "get_map_state");
                dispose = method(clazz, "dispose");
                getPData = method(clazz, "getPData");
            }

            return constructor;
        }
    }
}
