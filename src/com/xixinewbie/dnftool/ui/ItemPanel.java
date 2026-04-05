package com.xixinewbie.dnftool.ui;

import javax.swing.*;

/**
 * created by zhaoyuntao
 * on 2026/04/06
 */
public class ItemPanel extends JPanel {
    private UIFlusher call;
    
    public ItemPanel setFlushUI(UIFlusher call) {
        this.call = call;
        return this;
    }
    
    public void flushUI() {
        if (call != null) {
            call.onFlushUI();
        }
    }
    
    public interface UIFlusher {
        void onFlushUI();
    }
}
