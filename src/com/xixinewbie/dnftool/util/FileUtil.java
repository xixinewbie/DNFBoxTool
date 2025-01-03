package com.xixinewbie.dnftool.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public final static String FILE_PATH = "sprite.init";

    public static boolean write(String json) {
        if (S.isEmpty(json)) {
            return false;
        }
        try {
            Path path = Paths.get(FILE_PATH);
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

    public static void saveConfigToFile(String json, File file) {
        if (S.isEmpty(json)) {
            return;
        }
        try {
            Path path = Paths.get(file.getAbsolutePath());
            Files.writeString(path, json);
        } catch (Exception e) {
            S.e("save to file failed:" + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        boolean ok = write("hello");
//        S.s("write result: " + ok);
//        String json = read(FILE_PATH);
//        S.s("read result: " + json);
//    }
}
