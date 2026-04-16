package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.KeyEvent;
import com.xixinewbie.dnftool.model.Operation;
import com.xixinewbie.dnftool.model.Task;
import com.xixinewbie.dnftool.util.S;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskExecutor extends Thread {
    private boolean quit;
    long timeLastAction = 0;
    long timeLastActionExecuted = 0;
    private Task task;
    private Operation operation;
    private Operation operationPlaying;
    private final Callback callback;
    //确保每个按键按下后最终都能被抬起
    private final Set<KeyEvent> actionsDown = new HashSet<>();
    public static int mouseLastX = -1, mouseLastY = -1;
    
    private final boolean ignoreMouseMove;
    private final boolean highSpeed;
    private String quitMsg;
    
    private final int taskWindowWidth;
    
    public TaskExecutor(Task task, Operation operation, Callback callback) {
        this.task = task;
        this.taskWindowWidth = task.getGameWindowW();
        this.operation = operation;
        this.callback = callback;
        this.ignoreMouseMove = ActionManager.ignoreMove;
        this.highSpeed = ActionManager.highSpeed;
    }
    
    @Override
    public void run() {
        if (taskWindowWidth <= 0) {
            callback.onBreak("脚本信息缺失或者版本不匹配");
            return;
        }
        //第一次执行时，等待300毫秒，防止第一个event被忽略。
        S.sleep(300);
        int count = 0;
        if (operation != null) {
            runTask(operation, count);
        } else {
            while (!isQuit() && count < task.getCount()) {
                count++;
                List<Operation> operations = task.copy();
                if (operations != null) {
                    for (Operation operation : operations) {
                        if (!runTask(operation, count)) {
                            break;
                        }
                        S.sleep(200);
                    }
                }
            }
        }
        for (KeyEvent keyEventDown : actionsDown) {
            click(keyEventDown, true);
        }
        actionsDown.clear();
        boolean isBreak = isQuit();
        quit = true;
        if (isBreak) {
            callback.onBreak(quitMsg);
        } else {
            callback.onEnd();
        }
    }
    
    private boolean runTask(Operation operation, int countTask) {
        int countOperation = 0;
        //第一个执行任务
        if (this.operationPlaying == null) {
            //先把窗口调整到前排
            WindowPositionManager.setTop();
        }
        this.operationPlaying = operation;
        while (!isQuit() && countOperation < operation.getCount()) {
            countOperation++;
            timeLastActionExecuted = 0;//重置一下时间，否则当循环第二次时，时间会小于上一次。
            timeLastAction = 0;
            callback.onRun(operation, countOperation, countTask);
            //如果是睡眠任务，仅执行睡眠操作
            if (operation.getSleepTime() > 0) {
                S.sleep(operation.getSleepTime());
                continue;
            } else if (!runActions(operation.copyActions())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean runActions(List<KeyEvent> keyEvents) {
        if (keyEvents != null) {
            for (KeyEvent keyEvent : keyEvents) {
                if (!runAction(keyEvent)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean runAction(KeyEvent keyEvent) {
        if (ignoreMouseMove &&
                (keyEvent.action == KeyEvent.ACTION.move || keyEvent.action == KeyEvent.ACTION.up)) {
            return true;
        }
        if (isQuit()) {
            return false;
        }
        if (timeLastActionExecuted == 0) {
            timeLastActionExecuted = S.now();
        }
        if (timeLastAction == 0) {
            timeLastAction = keyEvent.time;
        }
        sleepBeforeAction(keyEvent);
        if (!click(keyEvent, false)) {
            return false;
        }
        sleepAfterAction(keyEvent);
        return !isQuit();
    }
    
    private void sleepBeforeAction(KeyEvent keyEvent) {
        long now = S.now();
        //应该等待多长时间
        long timeFromLastAction = keyEvent.time - timeLastAction;
        //实际已经过去多长时间
        long timeFromLastActionExecuted = now - timeLastActionExecuted;
        if (!ignoreMouseMove) {
            long timeToSleep = timeFromLastAction - timeFromLastActionExecuted;
            if (timeToSleep > 0) {
                S.sleep(timeToSleep);
            }
        }
    }
    
    private void sleepAfterAction(KeyEvent keyEvent) {
        if (ignoreMouseMove) {
            if (keyEvent.action == KeyEvent.ACTION.down) {
                long timeClickDelay = highSpeed ? 50 : 100;
                S.sleep(timeClickDelay);
                KeyEvent keyEventUp = new KeyEvent(keyEvent.type);
                keyEventUp.mouseButton = keyEvent.mouseButton;
                keyEventUp.key = keyEvent.key;
                keyEventUp.action = KeyEvent.ACTION.up;
                keyEventUp.x = keyEvent.x;
                keyEventUp.y = keyEvent.y;
                keyEventUp.time = keyEvent.time + timeClickDelay;
                click(keyEventUp, false);
                S.sleep(highSpeed ? 250 : 350);
            }
        }
    }
    
    public boolean click(KeyEvent keyEvent, boolean forceRelease) {
        timeLastAction = keyEvent.time;
        timeLastActionExecuted = S.now();
        //记录当前鼠标位置，如果手动移动鼠标，则终止任务
        if (mouseLastX < 0 || mouseLastY < 0) {
            WinDef.POINT point = new WinDef.POINT();
            User32.INSTANCE.GetCursorPos(point);
            mouseLastX = point.x;
            mouseLastY = point.y;
        }
        if (keyEvent.type == KeyEvent.TYPE.mouse) {
            float scalePercent = (float) WindowPositionManager.wGameWindow / taskWindowWidth;
            int x = (int) (keyEvent.x * scalePercent) + WindowPositionManager.xGameWindow;
            int y = (int) (keyEvent.y * scalePercent) + WindowPositionManager.yGameWindow;
            mouseLastX = x;
            mouseLastY = y;
            User32.INSTANCE.SetCursorPos(x, y);
            
            try {
                Robot robot = new Robot();
                if (keyEvent.action == KeyEvent.ACTION.up || forceRelease) {
                    if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.left) {
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    } else if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.right) {
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                    } else if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.middle) {
                        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                    }
                    actionsDown.remove(keyEvent);
                } else if (keyEvent.action == KeyEvent.ACTION.down) {
                    if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.left) {
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    } else if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.right) {
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    } else if (keyEvent.mouseButton == KeyEvent.MOUSE_BUTTON.middle) {
                        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                    }
                    actionsDown.add(keyEvent);
                }
            } catch (AWTException e) {
                return false;
            }
        } else if (keyEvent.type == KeyEvent.TYPE.keyboard) {
            try {
                Robot robot = new Robot();
                if (keyEvent.action == KeyEvent.ACTION.up || forceRelease) {
                    robot.keyRelease(keyEvent.key);
                    actionsDown.remove(keyEvent);
                } else if (keyEvent.action == KeyEvent.ACTION.down) {
                    robot.keyPress(keyEvent.key);
                    actionsDown.add(keyEvent);
                }
            } catch (AWTException e) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isPlayingWholeTask() {
        return operation == null;
    }
    
    public Operation getPlayingOperation() {
        return operationPlaying;
    }
    
    public Task getPlayingTask() {
        return task;
    }
    
    public boolean isQuit() {
        return quit;
    }
    
    public void setQuit(boolean quit, String msg) {
        this.quit = quit;
        this.quitMsg = msg;
    }
    
    public boolean isSameMousePosition(int x, int y) {
        return x == mouseLastX && y == mouseLastY;
    }
    
    public interface Callback {
        void onRun(Operation operation, int countOperation, int count);
        
        void onEnd();
        
        void onBreak(String quitMsg);
    }
}
