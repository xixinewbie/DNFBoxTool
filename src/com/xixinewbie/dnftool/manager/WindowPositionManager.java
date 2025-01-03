package com.xixinewbie.dnftool.manager;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class WindowPositionManager {

    public static final String GAME_NAME = "地下城与勇士：创新世纪";
    public static int xMouse, yMouse;
    public static int xMouseWindow, yMouseWindow;
    public static int xGameWindow, yGameWindow;
    public static int wGameWindow, hGameWindow;
    public static double scaleX, scaleY;
    public static boolean gameWindowExists;
    private static Boolean gameLastExists;
    public static WindowDetector detector;

    public static void init() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, GAME_NAME);
        WinDef.POINT point = new WinDef.POINT();
        User32.INSTANCE.GetCursorPos(point);
        xMouse = point.x;
        yMouse = point.y;
        GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        AffineTransform defaultTransform = defaultConfiguration.getDefaultTransform();
        scaleX = defaultTransform.getScaleX();
        scaleY = defaultTransform.getScaleY();

        if (hwnd != null) {
            gameWindowExists = true;
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            xGameWindow = rect.left;
            yGameWindow = rect.top;
            wGameWindow = rect.right - rect.left;
            hGameWindow = rect.bottom - rect.top;

            xMouseWindow = xMouse - xGameWindow;
            yMouseWindow = yMouse - yGameWindow;
        } else {
            gameWindowExists = false;
            xMouseWindow = xMouse;
            yMouseWindow = yMouse;
        }
        if (detector != null) {
            if (gameLastExists == null || gameWindowExists != gameLastExists) {
                gameLastExists = gameWindowExists;
                detector.onWindowChange(gameWindowExists);
            }
        }
    }

    public static void setTop() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, GAME_NAME);
        if (hwnd != null) {
            User32.INSTANCE.SetForegroundWindow(hwnd);
        }
    }

    public interface WindowDetector {
        void onWindowChange(boolean exists);
    }
}
