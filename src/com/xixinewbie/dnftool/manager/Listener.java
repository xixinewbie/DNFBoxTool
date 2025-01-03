package com.xixinewbie.dnftool.manager;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.xixinewbie.dnftool.util.S;

public class Listener {
    private static WinUser.HHOOK mouseHook;
    private static WinUser.HHOOK keyboardHook;
    private static MouseListener mouseListener;
    private static KeyboardListener keyboardListener;

    private static final Thread startRunnable = new Thread() {
        @Override
        public void run() {
            final User32 lib = User32.INSTANCE;

            WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);

            WinUser.LowLevelKeyboardProc lowLevelKeyboardProc = new WinUser.LowLevelKeyboardProc() {
                @Override
                public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT info) {
                    if (nCode >= 0) {
                        switch (wParam.intValue()) {
                            case WinUser.WM_KEYUP:
                                if (keyboardListener != null) {
                                    keyboardListener.onKeyUp(info.vkCode);
                                }
                                break;
                            case WinUser.WM_KEYDOWN:
                                if (keyboardListener != null) {
                                    keyboardListener.onKeyDown(info.vkCode);
                                }
                                break;
                            case WinUser.WM_SYSKEYUP:
                            case WinUser.WM_SYSKEYDOWN:
//                                S.e("in callback, key=" + info.vkCode);

                        }
                    }

                    Pointer ptr = info.getPointer();
                    long peer = Pointer.nativeValue(ptr);
                    return lib.CallNextHookEx(keyboardHook, nCode, wParam, new WinDef.LPARAM(peer));
                }
            };
            keyboardHook = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, lowLevelKeyboardProc, hMod, 0);

            S.s("Keyboard hook installed");

            WinUser.LowLevelMouseProc lowLevelMouseProc = new WinUser.LowLevelMouseProc() {

                @Override
                public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.MSLLHOOKSTRUCT lParam) {
                    if (nCode >= 0) {
                        WindowPositionManager.init();
                        int x = WindowPositionManager.xMouseWindow;
                        int y = WindowPositionManager.yMouseWindow;
                        switch (wParam.intValue()) {
                            case 512:
                                if (mouseListener != null) {
                                    mouseListener.onMove(x, y);
                                }
                                break;
                            case 513:
                                if (mouseListener != null) {
                                    mouseListener.onLeftDown(x, y);
                                }
                                break;
                            case 514:
                                if (mouseListener != null) {
                                    mouseListener.onLeftUp(x, y);
                                }
                                break;
                            case 516:
                                if (mouseListener != null) {
                                    mouseListener.onRightDown(x, y);
                                }
                                break;
                            case 517:
                                if (mouseListener != null) {
                                    mouseListener.onRightUp(x, y);
                                }
                                break;
                            case 519:
                                if (mouseListener != null) {
                                    mouseListener.onMiddleDown(x, y);
                                }
                                break;
                            case 520:
                                if (mouseListener != null) {
                                    mouseListener.onMiddleUp(x, y);
                                }
                                break;
                        }
                    }
                    return lib.CallNextHookEx(mouseHook, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
                }
            };
            mouseHook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_MOUSE_LL, lowLevelMouseProc, hMod, 0);

            // This bit never returns from GetMessage
            int result;
            WinUser.MSG msg = new WinUser.MSG();
            lib.GetMessage(msg, null, 0, 0);
            while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                    S.e("error in get message");
                    break;
                } else {
                    lib.TranslateMessage(msg);
                    lib.DispatchMessage(msg);
                }
            }
            lib.UnhookWindowsHookEx(keyboardHook);
            lib.UnhookWindowsHookEx(mouseHook);
        }
    };

    public static void start() {
        startRunnable.start();
    }

    public static void stop() {
        startRunnable.interrupt();
    }

    public static void setMouseListener(MouseListener listener) {
        mouseListener = listener;
    }

    public static void setKeyboardListener(KeyboardListener listener) {
        keyboardListener = listener;
    }

    public interface MouseListener {
        default void onRightDown(int x, int y) {
        }

        default void onRightUp(int x, int y) {
        }

        default void onLeftDown(int x, int y) {
        }

        default void onLeftUp(int x, int y) {
        }

        default void onMiddleDown(int x, int y) {
        }

        default void onMiddleUp(int x, int y) {
        }

        default void onMove(int x, int y) {
        }
    }

    public interface KeyboardListener {
        default void onKeyDown(int key) {
        }

        default void onKeyUp(int key) {
        }
    }
}
