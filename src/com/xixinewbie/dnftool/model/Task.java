package com.xixinewbie.dnftool.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task {
    private String name;
    public final long id;
    private int count;
    private List<Action> actions;
    private long sleepTime;
    private boolean access;

    public Task(long id) {
        this.id = id;
    }

    public void addAction(Action action) {
        synchronized (Task.class) {
            if (actions == null) {
                actions = new ArrayList<>();
            }
            actions.add(action);
        }
    }

    public void clearAction() {
        synchronized (Task.class) {
            if (actions != null) {
                actions.clear();
            }
        }
    }

    public List<Action> copyActions() {
        synchronized (Task.class) {
            return actions == null ? new ArrayList<>(0) : new ArrayList<>(actions);
        }
    }

    public void setActions(List<Action> actions) {
        synchronized (Task.class) {
            this.actions = actions;
        }
    }

    public int getCount() {
        return count;
    }

    public Task setCount(int count) {
        this.count = count;
        return this;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        synchronized (Task.class) {
            return this.actions == null ? 0 : this.actions.size();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public Task setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public boolean isAccess() {
        return access;
    }

    public Task setAccess(boolean access) {
        this.access = access;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }
}
