package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.util.JsonUtil;
import com.xixinewbie.dnftool.util.S;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * created by zhaoyuntao
 * on 2026/04/04
 */
public class StorageManager {
    
    public final static String FILE_EXTENSION = ".xxsp";
    public final static String FILE_PATH = "conf/sprite.conf";
    public final static String IMG_PATH = "img/";
    
    public static void loadAllSprites() {
        String json = StorageManager.read(FILE_PATH);
        ActionManager.tasks = JsonUtil.toTasks(json);
    }
    
    public static void saveToFile() {
        if (write(JsonUtil.toJson(ActionManager.tasks))) {
            S.s("保存成功");
        } else {
            S.e("保存失败");
        }
    }
    
    public static void saveToFile(String json, String path) {
        write(json, path);
    }
    
    public static boolean write(String json) {
        return write(json, FILE_PATH);
    }
    
    public static boolean write(String json, String pathString) {
        if (S.isEmpty(json)) {
            S.e("json is empty");
            return false;
        }
        try {
            Path path = Paths.get(pathString);
            Files.writeString(path, json);
        } catch (Exception e) {
            S.e("write to file failed:" + e.getMessage());
            return false;
        }
        return true;
    }
    
    public static String read(String filePath) {
        if (S.isEmpty(filePath)) {
            return null;
        }
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path);
        } catch (Exception e) {
            S.e("write to file failed:" + e.getMessage());
            return null;
        }
    }
    
    //    public static void main(String[] args) {
    //        boolean ok = write("hello");
    //        S.s("write result: " + ok);
    //        String json = read(FILE_PATH);
    //        S.s("read result: " + json);
    //    }
}
