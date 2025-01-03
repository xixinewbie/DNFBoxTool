package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.Global;
import com.xixinewbie.dnftool.model.Task;

public class ActionManager {
    public static Global global;

    public static synchronized void createTask(int position, Task task) {
        if (global != null) {
            global.addTask(position, task);
        }
    }

    public static synchronized void removeTask(Task task) {
        if (global != null) {
            global.removeTask(task);
        }
    }
}
