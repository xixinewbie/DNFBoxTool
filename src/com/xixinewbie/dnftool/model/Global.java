package com.xixinewbie.dnftool.model;

import com.xixinewbie.dnftool.util.S;

import java.util.ArrayList;
import java.util.List;

public class Global {
    public int count;
    public boolean ignoreMove;
    public boolean highSpeed;
    private List<Task> tasks;
    private int widthWindow;
    private int heightWindow;

    public void setTasks(List<Task> tasks) {
        synchronized (Global.class) {
            this.tasks = tasks;
        }
    }

    public List<Task> copy() {
        synchronized (Global.class) {
            return tasks == null ? new ArrayList<>(0) : new ArrayList<>(tasks);
        }
    }

    public void addTask(int position, Task task) {
        synchronized (Global.class) {
            if (tasks == null) {
                tasks = new ArrayList<>();
            }
            if (position >= 0 && position < tasks.size()) {
                tasks.add(position, task);
            } else {
                tasks.add(task);
            }
        }
    }

    public void clearTask() {
        synchronized (Global.class) {
            if (tasks != null) {
                tasks.clear();
            }
        }
    }

    public void removeTask(Task task) {
        synchronized (Global.class) {
            if (tasks != null) {
                tasks.remove(task);
            }
        }
        S.s("removed:" + task.getName() + " rest:" + tasks);
    }

    public int getTaskSize() {
        synchronized (Global.class) {
            return tasks == null ? 0 : tasks.size();
        }
    }

    public int accessTaskSize() {
        synchronized (Global.class) {
            if (tasks == null) {
                return 0;
            }
            int size = 0;
            for (Task task : tasks) {
                if (task.isAccess()) {
                    size++;
                }
            }
            return size;
        }
    }

    public int getWidthWindow() {
        return widthWindow;
    }

    public void setWidthWindow(int widthWindow) {
        this.widthWindow = widthWindow;
    }

    public int getHeightWindow() {
        return heightWindow;
    }

    public void setHeightWindow(int heightWindow) {
        this.heightWindow = heightWindow;
    }
}
