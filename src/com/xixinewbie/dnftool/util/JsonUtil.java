package com.xixinewbie.dnftool.util;

import com.xixinewbie.dnftool.manager.ActionManager;
import com.xixinewbie.dnftool.manager.WindowPositionManager;
import com.xixinewbie.dnftool.model.KeyEvent;
import com.xixinewbie.dnftool.model.Operation;
import com.xixinewbie.dnftool.model.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JsonUtil {
    
    public static JSONObject toJson(Task task) {
        List<Operation> list = task.copy();
        JSONObject taskObject = new JSONObject();
        taskObject.put("createTime", task.getCreateTime());
        taskObject.put("editTime", task.getEditTime());
        taskObject.put("count", task.getCount());
        taskObject.put("name", task.getName());
        taskObject.put("icon", task.getIcon());
        taskObject.put("wWindow", task.getGameWindowW());
        taskObject.put("hWindow", task.getGameWindowH());
        JSONArray operationArray = new JSONArray();
        taskObject.put("operations", operationArray);
        if (list != null) {
            for (Operation operation : list) {
                JSONObject operationObject = new JSONObject();
                operationObject.put("createTime", operation.getCreateTime());
                operationObject.put("count", operation.getCount());
                operationObject.put("sleepTime", operation.getSleepTime());
                operationObject.put("name", operation.getName());
                operationArray.put(operationObject);
                
                List<KeyEvent> keyEvents = operation.copyActions();
                if (keyEvents != null) {
                    JSONArray eventArray = new JSONArray();
                    operationObject.put("events", eventArray);
                    for (KeyEvent keyEvent : keyEvents) {
                        JSONObject actionObject = getEventObject(keyEvent);
                        eventArray.put(actionObject);
                    }
                }
            }
        }
        return taskObject;
    }
    
    public static String toJson(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ignoreMove", ActionManager.ignoreMove);
        jsonObject.put("highSpeed", ActionManager.highSpeed);
        JSONArray taskArray = new JSONArray();
        jsonObject.put("tasks", taskArray);
        
        for (Task task : tasks) {
            taskArray.put(toJson(task));
        }
        return jsonObject.toString(2);
    }
    
    private static JSONObject getEventObject(KeyEvent keyEvent) {
        JSONObject actionObject = new JSONObject();
        actionObject.put("index", keyEvent.index);
        actionObject.put("type", keyEvent.type);
        actionObject.put("key", keyEvent.key);
        actionObject.put("time", keyEvent.time);
        actionObject.put("x", keyEvent.x);
        actionObject.put("y", keyEvent.y);
        actionObject.put("mouseButton", keyEvent.mouseButton);
        actionObject.put("action", keyEvent.action);
        return actionObject;
    }
    
    public static Task toTask(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
        } catch (Throwable e) {
            return null;
        }
        return toTask(jsonObject);
    }
    
    public static Task toTask(JSONObject taskObject) {
        Task task = new Task();
        
        task.setCount(taskObject.optInt("count"))
                .setName(taskObject.optString("name"))
                .setIcon(taskObject.optString("icon"))
                .setCreateTime(taskObject.optLong("createTime"))
                .setEditTime(taskObject.optLong("editTime"))
                .setWindowSize(taskObject.optInt("wWindow"), taskObject.optInt("hWindow"));
        List<Operation> operations = new ArrayList<>();
        task.setOperations(operations);
        
        JSONArray operaitionArray = taskObject.optJSONArray("operations");
        if (operaitionArray == null || operaitionArray.isEmpty()) {
            return task;
        }
        for (int j = 0; j < operaitionArray.length(); j++) {
            JSONObject operationObject = operaitionArray.optJSONObject(j);
            if (operationObject != null) {
                Operation operation = new Operation(operationObject.optLong("createTime"));
                operations.add(operation);
                operation.setName(operationObject.optString("name"));
                operation.setCount(operationObject.optInt("count"));
                operation.setSleepTime(operationObject.optLong("sleepTime"));
                JSONArray eventArray = operationObject.optJSONArray("events");
                if (eventArray != null) {
                    List<KeyEvent> keyEvents = new ArrayList<>(eventArray.length());
                    operation.setActions(keyEvents);
                    for (int k = 0; k < eventArray.length(); k++) {
                        JSONObject actionObject = eventArray.optJSONObject(k);
                        KeyEvent keyEvent = new KeyEvent(actionObject.optInt("type"));
                        keyEvent.index = actionObject.optInt("index");
                        keyEvent.key = actionObject.optInt("key");
                        keyEvent.time = actionObject.optLong("time");
                        keyEvent.x = actionObject.optInt("x");
                        keyEvent.y = actionObject.optInt("y");
                        keyEvent.mouseButton = actionObject.optInt("mouseButton");
                        keyEvent.action = actionObject.optInt("action");
                        keyEvents.add(keyEvent);
                    }
                }
            }
        }
        return task;
    }
    
    public static List<Task> toTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
        } catch (Throwable e) {
            WindowPositionManager.init();
            ActionManager.ignoreMove = true;
            ActionManager.highSpeed = true;
            return tasks;
        }
        ActionManager.ignoreMove = jsonObject.optBoolean("ignoreMove");
        ActionManager.highSpeed = jsonObject.optBoolean("highSpeed");
        JSONArray tasksArray = jsonObject.optJSONArray("tasks");
        if (tasksArray == null || tasksArray.isEmpty()) {
            return tasks;
        }
        for (int i = 0; i < tasksArray.length(); i++) {
            tasks.add(toTask(tasksArray.optJSONObject(i)));
        }
        tasks.sort(Comparator.comparingLong(Task::getCreateTime));
        return tasks;
    }
}
