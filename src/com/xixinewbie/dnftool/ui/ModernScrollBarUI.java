package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.ColorUIManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {
    
    private final Color thumbColor = ColorUIManager.colorScrollBar; // 滑块颜色
    private final Color trackColor = ColorUIManager.colorBackground;
    
//    @Override
//    protected void configureScrollBarParameters() {
//        super.configureScrollBarParameters();
//        scrollbar.setOpaque(false); // 设置滚动条透明
//    }
    
    
    @Override
    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
        scrollbar.setOpaque(false); // 设置滚动条透明
    }
    
    // 设置滚动条的宽度
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(8, 0); // 宽度设为 8 像素，更精致
    }
    
    // 绘制滑块
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置滑块颜色和圆角
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x , thumbBounds.y + 1,
                thumbBounds.width - 2, thumbBounds.height - 2, 0, 0);
        g2.dispose();
    }
    
    // 绘制轨道 (设置为空，实现极简效果)
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
    
    // 隐藏顶部的箭头按钮
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    
    // 隐藏底部的箭头按钮
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}