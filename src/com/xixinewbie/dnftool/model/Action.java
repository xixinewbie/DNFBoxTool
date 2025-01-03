package com.xixinewbie.dnftool.model;

import java.util.Objects;

public class Action {
    public int index;
    @TYPE
    public final int type;
    public int key;
    public long time;
    public int x, y;
    @MOUSE_BUTTON
    public int mouseButton;
    @ACTION
    public int action;

    public Action(@TYPE int type) {
        this.type = type;
    }

    public @interface TYPE {
        int mouse = 1;
        int keyboard = 2;
    }

    public @interface MOUSE_BUTTON {
        int left = 1;
        int right = 2;
        int middle = 3;
    }

    public @interface ACTION {
        int down = 1;
        int up = 2;
        int move = 3;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return type == action.type && key == action.key && mouseButton == action.mouseButton;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key, mouseButton);
    }

    @Override
    public String toString() {
        return type == TYPE.mouse ? "mouse(" + x + "," + y + ")" : "keyboard(" + key + ")";
    }
}
