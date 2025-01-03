package com.xixinewbie.dnftool.util;

import com.xixinewbie.dnftool.manager.WindowPositionManager;
import com.xixinewbie.dnftool.model.Action;
import com.xixinewbie.dnftool.model.Global;
import com.xixinewbie.dnftool.model.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    public static String toJson(Global global) {
        if (global == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", global.count);
        jsonObject.put("wWindow", global.getWidthWindow());
        jsonObject.put("hWindow", global.getHeightWindow());
        jsonObject.put("ignoreMove", global.ignoreMove);
        jsonObject.put("highSpeed", global.highSpeed);
        JSONArray taskArray = new JSONArray();
        jsonObject.put("tasks", taskArray);

        List<Task> list = global.copy();
        if (list != null) {
            for (Task task : list) {
                JSONObject taskObject = new JSONObject();
                taskObject.put("id", task.id);
                taskObject.put("count", task.getCount());
                taskObject.put("access", task.isAccess());
                taskObject.put("sleepTime", task.getSleepTime());
                taskObject.put("name", task.getName());
                taskArray.put(taskObject);

                List<Action> actions = task.copyActions();
                if (actions != null) {
                    JSONArray actionArray = new JSONArray();
                    taskObject.put("actions", actionArray);
                    for (Action action : actions) {
                        JSONObject actionObject = getActionObject(action);
                        actionArray.put(actionObject);
                    }
                }
            }
        }
        return jsonObject.toString(2);
    }

    private static JSONObject getActionObject(Action action) {
        JSONObject actionObject = new JSONObject();
        actionObject.put("index", action.index);
        actionObject.put("type", action.type);
        actionObject.put("key", action.key);
        actionObject.put("time", action.time);
        actionObject.put("x", action.x);
        actionObject.put("y", action.y);
        actionObject.put("mouseButton", action.mouseButton);
        actionObject.put("action", action.action);
        return actionObject;
    }

    public static Global toGlobal(String json) {
        Global global = new Global();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
        } catch (Throwable e) {
            WindowPositionManager.init();
            global.ignoreMove = true;
            global.highSpeed = true;
            global.setWidthWindow(WindowPositionManager.wGameWindow);
            global.setHeightWindow(WindowPositionManager.hGameWindow);
            global.count = 1;
            return global;
        }
        global.count = jsonObject.optInt("count");
        global.setWidthWindow(jsonObject.optInt("wWindow"));
        global.setHeightWindow(jsonObject.optInt("hWindow"));
        global.ignoreMove = jsonObject.optBoolean("ignoreMove");
        global.highSpeed = jsonObject.optBoolean("highSpeed");
        JSONArray tasksArray = jsonObject.optJSONArray("tasks");
        if (tasksArray == null) {
            return global;
        }
        List<Task> tasks = new ArrayList<>();
        global.setTasks(tasks);
        for (int i = 0; i < tasksArray.length(); i++) {
            JSONObject taskObject = tasksArray.optJSONObject(i);
            if (taskObject != null) {
                Task task = new Task(taskObject.optLong("id"));
                task.setName(taskObject.optString("name"));
                task.setAccess(taskObject.optBoolean("access"));
                task.setCount(taskObject.optInt("count"));
                task.setSleepTime(taskObject.optLong("sleepTime"));
                tasks.add(task);
                JSONArray actionArray = taskObject.optJSONArray("actions");
                if (actionArray != null) {
                    List<Action> actions = new ArrayList<>(actionArray.length());
                    task.setActions(actions);
                    for (int j = 0; j < actionArray.length(); j++) {
                        JSONObject actionObject = actionArray.optJSONObject(j);
                        Action action = new Action(actionObject.optInt("type"));
                        action.action = actionObject.optInt("index");
                        action.key = actionObject.optInt("key");
                        action.time = actionObject.optLong("time");
                        action.x = actionObject.optInt("x");
                        action.y = actionObject.optInt("y");
                        action.mouseButton = actionObject.optInt("mouseButton");
                        action.action = actionObject.optInt("action");
                        actions.add(action);
                    }
                }

            }
        }
        return global;
    }
}
