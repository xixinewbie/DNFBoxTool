package com.xixinewbie.dnftool.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task {
    private int count;
    private long createTime;
    private long editTime;
    private String name;
    private String icon;
    private List<Operation> operations;
    private int wGameWindow, hGameWindow;
    
    public void setOperations(List<Operation> operations) {
        synchronized (Task.class) {
            this.operations = operations;
        }
    }
    
    public List<Operation> copy() {
        synchronized (Task.class) {
            return operations == null ? new ArrayList<>(0) : new ArrayList<>(operations);
        }
    }
    
    public void addOperation(int position, Operation operation) {
        synchronized (Task.class) {
            if (operations == null) {
                operations = new ArrayList<>();
            }
            if (position >= 0 && position < operations.size()) {
                operations.add(position, operation);
            } else {
                operations.add(operation);
            }
        }
    }
    
    public void clearOperation() {
        synchronized (Task.class) {
            if (operations != null) {
                operations.clear();
            }
        }
    }
    
    public void removeOperation(Operation operation) {
        synchronized (Task.class) {
            if (operations != null) {
                operations.remove(operation);
            }
        }
    }
    
    public boolean contains(Operation operation) {
        synchronized (Task.class) {
            return operations != null && operations.contains(operation);
        }
    }
    
    public int getOperationSize() {
        synchronized (Task.class) {
            return operations == null ? 0 : operations.size();
        }
    }
    
    public boolean isEnpty() {
        return operations == null || operations.isEmpty();
    }
    
    public String getName() {
        return name;
    }
    
    public Task setName(String name) {
        this.name = name;
        return this;
    }
    
    public int getCount() {
        return count;
    }
    
    public Task setCount(int count) {
        this.count = count;
        return this;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public Task setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public long getEditTime() {
        return editTime;
    }
    
    public Task setEditTime(long editTime) {
        this.editTime = editTime;
        return this;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public Task setIcon(String icon) {
        this.icon = icon;
        return this;
    }
    
    public int getGameWindowH() {
        return hGameWindow;
    }
    
    public int getGameWindowW() {
        return wGameWindow;
    }
    
    public Task setWindowSize(int wGameWindow, int hGameWindow) {
        this.wGameWindow = wGameWindow;
        this.hGameWindow = hGameWindow;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return createTime == task.createTime;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(createTime);
    }
}
