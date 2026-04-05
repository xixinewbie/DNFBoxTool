package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.KeyEvent;
import com.xixinewbie.dnftool.model.Operation;
import com.xixinewbie.dnftool.model.Task;
import com.xixinewbie.dnftool.util.S;

public class RecordManager {
    
    private static boolean isRecording;
    
    public static void startWaitingRecording(Operation operation, Callback callback) {
        isRecording = true;
        callback.onWaiting(operation);
        Listener.setKeyboardListener(new Listener.KeyboardListener() {
            @Override
            public void onKeyUp(int key) {
                if (key == java.awt.event.KeyEvent.VK_F12) {
                    operation.clearAction();
                    startRecording(operation, callback);
                    callback.onStart(operation);
                } else if (key == java.awt.event.KeyEvent.VK_ESCAPE || key == java.awt.event.KeyEvent.VK_PAUSE) {
                    Listener.setKeyboardListener(null);
                    isRecording = false;
                    callback.onCancel(operation);
                }
            }
        });
    }
    
    public static void startRecording(Operation operation, Callback callback) {
        WindowPositionManager.setTop();
        Listener.setMouseListener(new Listener.MouseListener() {
            
            private boolean leftDown;
            private boolean rightDown;
            private boolean middleDown;
            
            @Override
            public void onMove(int x, int y) {
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.action = KeyEvent.ACTION.move;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onLeftDown(int x, int y) {
                if (leftDown) {
                    return;
                }
                leftDown = true;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.left;
                keyEvent.action = KeyEvent.ACTION.down;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onLeftUp(int x, int y) {
                if (!leftDown) {
                    return;
                }
                leftDown = false;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.left;
                keyEvent.action = KeyEvent.ACTION.up;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onRightDown(int x, int y) {
                if (rightDown) {
                    return;
                }
                rightDown = true;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.right;
                keyEvent.action = KeyEvent.ACTION.down;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onRightUp(int x, int y) {
                if (!rightDown) {
                    return;
                }
                rightDown = false;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.right;
                keyEvent.action = KeyEvent.ACTION.up;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onMiddleDown(int x, int y) {
                if (middleDown) {
                    return;
                }
                middleDown = true;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.middle;
                keyEvent.action = KeyEvent.ACTION.down;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
            
            @Override
            public void onMiddleUp(int x, int y) {
                if (!middleDown) {
                    return;
                }
                middleDown = false;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.mouse);
                keyEvent.mouseButton = KeyEvent.MOUSE_BUTTON.middle;
                keyEvent.action = KeyEvent.ACTION.up;
                keyEvent.x = x;
                keyEvent.y = y;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
        });
        
        Listener.setKeyboardListener(new Listener.KeyboardListener() {
            
            @Override
            public void onKeyDown(int key) {
                if (key == java.awt.event.KeyEvent.VK_PAUSE || key == java.awt.event.KeyEvent.VK_F12) {
                    Listener.setKeyboardListener(null);
                    Listener.setMouseListener(null);
                    isRecording = false;
                    callback.onEnd(operation);
                } else {
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.keyboard);
                    keyEvent.key = key;
                    keyEvent.action = KeyEvent.ACTION.down;
                    keyEvent.time = S.now();
                    operation.addAction(keyEvent);
                }
            }
            
            @Override
            public void onKeyUp(int key) {
                KeyEvent keyEvent = new KeyEvent(KeyEvent.TYPE.keyboard);
                keyEvent.key = key;
                keyEvent.action = KeyEvent.ACTION.up;
                keyEvent.time = S.now();
                operation.addAction(keyEvent);
            }
        });
    }
    
    public static boolean isRecording() {
        return isRecording;
    }
    
    public interface Callback {
        void onWaiting(Operation operation);
        
        void onStart(Operation operation);
        
        void onCancel(Operation operation);
        
        void onEnd(Operation operation);
    }
}
