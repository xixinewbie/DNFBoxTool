package com.xixinewbie.dnftool.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Operation {
    private String name;
    private final long createTime;
    private int count;
    private List<KeyEvent> keyEvents;
    private long sleepTime;

    public Operation(long createTime) {
        this.createTime = createTime;
    }

    public void addAction(KeyEvent keyEvent) {
        synchronized (Operation.class) {
            if (keyEvents == null) {
                keyEvents = new ArrayList<>();
            }
            keyEvents.add(keyEvent);
        }
    }

    public void clearAction() {
        synchronized (Operation.class) {
            if (keyEvents != null) {
                keyEvents.clear();
            }
        }
    }

    public List<KeyEvent> copyActions() {
        synchronized (Operation.class) {
            return keyEvents == null ? new ArrayList<>(0) : new ArrayList<>(keyEvents);
        }
    }

    public void setActions(List<KeyEvent> keyEvents) {
        synchronized (Operation.class) {
            this.keyEvents = keyEvents;
        }
    }

    public int getCount() {
        return count;
    }

    public Operation setCount(int count) {
        this.count = count;
        return this;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        synchronized (Operation.class) {
            return this.keyEvents == null ? 0 : this.keyEvents.size();
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

    public Operation setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return createTime == operation.createTime;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(createTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }
}
