package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.util.S;
import com.xixinewbie.dnftool.model.Action;
import com.xixinewbie.dnftool.model.Task;

import java.awt.event.KeyEvent;

public class RecordManager {

    private static boolean isRecording;

    public static void startWaitingRecording(Task task, Callback callback) {
        isRecording = true;
        callback.onWaiting(task);
        Listener.setKeyboardListener(new Listener.KeyboardListener() {
            @Override
            public void onKeyUp(int key) {
                if (key == KeyEvent.VK_F12) {
                    task.clearAction();
                    startRecording(task, callback);
                    callback.onStart(task);
                } else if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_PAUSE) {
                    Listener.setKeyboardListener(null);
                    isRecording = false;
                    callback.onCancel(task);
                }
            }
        });
    }

    public static void startRecording(Task task, Callback callback) {
        WindowPositionManager.setTop();
        Listener.setMouseListener(new Listener.MouseListener() {

            private boolean leftDown;
            private boolean rightDown;
            private boolean middleDown;

            @Override
            public void onMove(int x, int y) {
                Action action = new Action(Action.TYPE.mouse);
                action.action = Action.ACTION.move;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onLeftDown(int x, int y) {
                if (leftDown) {
                    return;
                }
                leftDown = true;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.left;
                action.action = Action.ACTION.down;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onLeftUp(int x, int y) {
                if (!leftDown) {
                    return;
                }
                leftDown = false;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.left;
                action.action = Action.ACTION.up;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onRightDown(int x, int y) {
                if (rightDown) {
                    return;
                }
                rightDown = true;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.right;
                action.action = Action.ACTION.down;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onRightUp(int x, int y) {
                if (!rightDown) {
                    return;
                }
                rightDown = false;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.right;
                action.action = Action.ACTION.up;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onMiddleDown(int x, int y) {
                if (middleDown) {
                    return;
                }
                middleDown = true;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.middle;
                action.action = Action.ACTION.down;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }

            @Override
            public void onMiddleUp(int x, int y) {
                if (!middleDown) {
                    return;
                }
                middleDown = false;
                Action action = new Action(Action.TYPE.mouse);
                action.mouseButton = Action.MOUSE_BUTTON.middle;
                action.action = Action.ACTION.up;
                action.x = x;
                action.y = y;
                action.time = S.now();
                task.addAction(action);
            }
        });

        Listener.setKeyboardListener(new Listener.KeyboardListener() {

            @Override
            public void onKeyDown(int key) {
                if (key == KeyEvent.VK_PAUSE || key == KeyEvent.VK_F12) {
                    Listener.setKeyboardListener(null);
                    Listener.setMouseListener(null);
                    isRecording = false;
                    callback.onEnd(task);
                } else {
                    Action action = new Action(Action.TYPE.keyboard);
                    action.key = key;
                    action.action = Action.ACTION.down;
                    action.time = S.now();
                    task.addAction(action);
                }
            }

            @Override
            public void onKeyUp(int key) {
                Action action = new Action(Action.TYPE.keyboard);
                action.key = key;
                action.action = Action.ACTION.up;
                action.time = S.now();
                task.addAction(action);
            }
        });
    }

    public static boolean isRecording() {
        return isRecording;
    }

    public interface Callback {
        void onWaiting(Task task);

        void onStart(Task task);

        void onCancel(Task task);

        void onEnd(Task task);
    }
}
