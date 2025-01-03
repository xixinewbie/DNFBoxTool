package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.util.S;
import com.xixinewbie.dnftool.model.Action;
import com.xixinewbie.dnftool.model.Task;
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
    private Task taskPlaying;
    private final Callback callback;
    //确保每个按键按下后最终都能被抬起
    private final Set<Action> actionsDown = new HashSet<>();
    public static int mouseLastX = -1, mouseLastY = -1;

    private final boolean ignoreMouseMove;
    private final boolean highSpeed;
    private String quitMsg;

    public TaskExecutor(Task taskPlaying, Callback callback) {
        this.taskPlaying = taskPlaying;
        this.callback = callback;
        this.ignoreMouseMove = ActionManager.global.ignoreMove;
        this.highSpeed = ActionManager.global.highSpeed;
    }

    @Override
    public void run() {
        //第一次执行时，等待300毫秒，防止第一个action被忽略。
        S.sleep(300);
        int count = 0;
        if (taskPlaying != null) {
            runTask(taskPlaying);
        } else {
            while (!isQuit() && count < ActionManager.global.count) {
                count++;
                List<Task> tasks = ActionManager.global.copy();
                if (tasks != null) {
                    for (Task task : tasks) {
                        if (!runTask(task)) {
                            break;
                        }
                    }
                }
            }
        }
        for (Action actionDown : actionsDown) {
            click(actionDown, true);
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

    private boolean runTask(Task task) {
        if (!task.isAccess()) {
            return true;
        }
        int countTask = 0;
        //第一个执行任务
        if (this.taskPlaying == null) {
            //先把窗口调整到前排
            WindowPositionManager.setTop();
        }
        this.taskPlaying = task;
        while (!isQuit() && countTask < task.getCount()) {
            countTask++;
            timeLastActionExecuted = 0;//重置一下时间，否则当循环第二次时，时间会小于上一次。
            timeLastAction = 0;
            callback.onRun(task, countTask);
            //如果是睡眠任务，仅执行睡眠操作
            if (task.getSleepTime() > 0) {
                S.sleep(task.getSleepTime());
                continue;
            } else if (!runActions(task.copyActions())) {
                return false;
            }
        }
        return true;
    }

    private boolean runActions(List<Action> actions) {
        if (actions != null) {
            for (Action action : actions) {
                if (!runAction(action)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean runAction(Action action) {
        if (ignoreMouseMove &&
                (action.action == Action.ACTION.move || action.action == Action.ACTION.up)) {
            return true;
        }
        if (isQuit()) {
            return false;
        }
        if (timeLastActionExecuted == 0) {
            timeLastActionExecuted = S.now();
        }
        if (timeLastAction == 0) {
            timeLastAction = action.time;
        }
        sleepBeforeAction(action);
        if (!click(action, false)) {
            return false;
        }
        sleepAfterAction(action);
        return !isQuit();
    }

    private void sleepBeforeAction(Action action) {
        long now = S.now();
        //应该等待多长时间
        long timeFromLastAction = action.time - timeLastAction;
        //实际已经过去多长时间
        long timeFromLastActionExecuted = now - timeLastActionExecuted;
        if (!ignoreMouseMove) {
            long timeToSleep = timeFromLastAction - timeFromLastActionExecuted;
            if (timeToSleep > 0) {
                S.sleep(timeToSleep);
            }
        }
    }

    private void sleepAfterAction(Action action) {
        if (ignoreMouseMove) {
            if (action.action == Action.ACTION.down) {
                long timeClickDelay = highSpeed ? 50 : 100;
                S.sleep(timeClickDelay);
                Action actionUp = new Action(action.type);
                actionUp.mouseButton = action.mouseButton;
                actionUp.key = action.key;
                actionUp.action = Action.ACTION.up;
                actionUp.x = action.x;
                actionUp.y = action.y;
                actionUp.time = action.time + timeClickDelay;
                click(actionUp, false);
                S.sleep(highSpeed ? 250 : 350);
            }
        }
    }

    public boolean click(Action action, boolean forceRelease) {
        timeLastAction = action.time;
        timeLastActionExecuted = S.now();
        //记录当前鼠标位置，如果手动移动鼠标，则终止任务
        if (mouseLastX < 0 || mouseLastY < 0) {
            WinDef.POINT point = new WinDef.POINT();
            User32.INSTANCE.GetCursorPos(point);
            mouseLastX = point.x;
            mouseLastY = point.y;
        }
        if (action.type == Action.TYPE.mouse) {
            float scalePercent = (float) WindowPositionManager.wGameWindow / ActionManager.global.getWidthWindow();
            int x = (int) (action.x * scalePercent) + WindowPositionManager.xGameWindow;
            int y = (int) (action.y * scalePercent) + WindowPositionManager.yGameWindow;
            mouseLastX = x;
            mouseLastY = y;
            User32.INSTANCE.SetCursorPos(x, y);

            try {
                Robot robot = new Robot();
                if (action.action == Action.ACTION.up || forceRelease) {
                    if (action.mouseButton == Action.MOUSE_BUTTON.left) {
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    } else if (action.mouseButton == Action.MOUSE_BUTTON.right) {
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                    } else if (action.mouseButton == Action.MOUSE_BUTTON.middle) {
                        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                    }
                    actionsDown.remove(action);
                } else if (action.action == Action.ACTION.down) {
                    if (action.mouseButton == Action.MOUSE_BUTTON.left) {
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    } else if (action.mouseButton == Action.MOUSE_BUTTON.right) {
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    } else if (action.mouseButton == Action.MOUSE_BUTTON.middle) {
                        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                    }
                    actionsDown.add(action);
                }
            } catch (AWTException e) {
                return false;
            }
        } else if (action.type == Action.TYPE.keyboard) {
            try {
                Robot robot = new Robot();
                if (action.action == Action.ACTION.up || forceRelease) {
                    robot.keyRelease(action.key);
                    actionsDown.remove(action);
                } else if (action.action == Action.ACTION.down) {
                    robot.keyPress(action.key);
                    actionsDown.add(action);
                }
            } catch (AWTException e) {
                return false;
            }
        }

        return true;
    }

    public Task getPlayingTask() {
        return taskPlaying;
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
        void onRun(Task task, int count);

        void onEnd();

        void onBreak(String quitMsg);
    }
}
