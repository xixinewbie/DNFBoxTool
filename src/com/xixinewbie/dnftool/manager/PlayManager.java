package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.Operation;
import com.xixinewbie.dnftool.model.Task;
import com.xixinewbie.dnftool.util.S;

import java.awt.event.KeyEvent;

public class PlayManager {
    
    private static TaskExecutor taskExecutor;
    
    public static void startPlay(Task task, Operation operation, TaskExecutor.Callback callback) {
        WindowPositionManager.setTop();
        Listener.setMouseListener(null);
        Listener.setKeyboardListener(new Listener.KeyboardListener() {
            @Override
            public void onKeyUp(int key) {
                if (key == KeyEvent.VK_PAUSE) {
                    stop("用户按下Pause终止了脚本");
                }
            }
        });
        Listener.setMouseListener(new Listener.MouseListener() {
            private final long timeStart = S.now();
            
            @Override
            public void onMove(int x, int y) {
                if ((S.now() - timeStart) > 1000) {
                    if (taskExecutor != null && !taskExecutor.isSameMousePosition(x, y)) {
                        stop("用户移动鼠标终止了脚本");
                    }
                }
            }
        });
        taskExecutor = new TaskExecutor(task, operation, callback);
        taskExecutor.start();
    }
    
    public static boolean isPlaying() {
        return taskExecutor != null && !taskExecutor.isQuit();
    }
    
    public static boolean isPlayingWholeTask() {
        return taskExecutor != null && taskExecutor.isPlayingWholeTask();
    }
    
    public static Operation getPlayingOperation() {
        return taskExecutor == null ? null : taskExecutor.getPlayingOperation();
    }
    
    public static Task getPlayingTask() {
        return taskExecutor == null ? null : taskExecutor.getPlayingTask();
    }
    
    public static void stop(String msg) {
        if (taskExecutor != null) {
            taskExecutor.setQuit(true, msg);
            taskExecutor.interrupt();
        }
        Listener.setKeyboardListener(null);
        Listener.setMouseListener(null);
    }
}
