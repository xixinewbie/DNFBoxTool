package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.ColorUIManager;
import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class SmoothSwitch extends JToggleButton {
    private float location = 0f;
    private final Timer timer;
    
    private final Color colorOn = ColorUIManager.colorSwitch;
    private final Color colorOnDisabled = ColorUIManager.colorSwitchDisabled;
    private final Color colorOff = ColorUIManager.colorSwitchOff;
    private final Color colorThumb = Color.WHITE;
    
    private final int fixedHeight = 22;
    private final int switchWidth = 45; // 按钮轨道的固定显示宽度
    
    public SmoothSwitch() {
        // 设置默认首选大小，高度固定为 22
        setPreferredSize(new Dimension(switchWidth, fixedHeight));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setText("==");
        setBorder(null);
        setOpaque(false);
        setFocusable(false);
        
        timer = new Timer(15, null);
        timer.addActionListener(e -> {
            if (isSelected()) {
                location += 0.15f;
                if (location >= 1.0f) {
                    location = 1.0f;
                    timer.stop();
                }
            } else {
                location -= 0.15f;
                if (location <= 0.0f) {
                    location = 0.0f;
                    timer.stop();
                }
            }
            repaint();
        });
        
        addActionListener(e -> {
            if (!timer.isRunning()) {
                timer.start();
            }
        });
    }
    
    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        location = b ? 1.0f : 0.0f;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        // 获取组件当前的实际宽高
        int width = getWidth();
        int height = getHeight();
        if (S.debugColor) {
            g2.setColor(Color.YELLOW);
            g2.fillRect(0, 0, width, height);
        }
        int radius = (int) Math.min((height - 4) / 2f, (width - 4) / 2f);
        // 计算居中绘图的起始坐标
        // 保持轨道宽度为 switchWidth，高度为 fixedHeight
        int x = 2;
        int y = (height - radius * 2) / 2;
        
        // 1. 绘制背景轨道
        Color bgColor = getIntermediateColor(colorOff, isEnabled() ? colorOn : colorOnDisabled, location);
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Double(x, y, width - 4, height - 4, radius * 2, radius * 2));
        
        // 2. 计算滑块参数
        int thumbSize = (int) (radius * 0.75f * 2);
        float thumbXStart = x + radius * 0.25f;
        float thumbXEnd = width - 2 - radius * 0.25f - thumbSize;
        float thumbX = thumbXStart + (thumbXEnd - thumbXStart) * location;
        float thumbY = (height - thumbSize) / 2f;
        
        // 3. 绘制滑块阴影 (稍微偏移一点)
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new Ellipse2D.Double(thumbX, thumbY + 1, thumbSize, thumbSize));
        
        // 4. 绘制滑块本体
        g2.setColor(colorThumb);
        g2.fill(new Ellipse2D.Double(thumbX, thumbY, thumbSize, thumbSize));
//
        g2.dispose();
    }
    
    private Color getIntermediateColor(Color c1, Color c2, float fraction) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
        return new Color(Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b)));
    }
}
