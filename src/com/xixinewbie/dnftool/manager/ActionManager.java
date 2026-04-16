package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ActionManager {
    public static List<Task> tasks = new ArrayList<>();
    public static boolean ignoreMove;
    public static boolean highSpeed;
    
    public static synchronized void addTask(Task task) {
        if (tasks != null) {
            tasks.add(task);
        }
    }
    
    public static synchronized void removeTask(Task task) {
        if (tasks != null) {
            tasks.remove(task);
        }
    }
}
