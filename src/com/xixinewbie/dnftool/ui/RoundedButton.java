package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.ColorUIManager;
import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private int radius = 5; // 圆角半径
    private boolean loading = false;
    private ScaledIcon loadingIcon;
    private Icon originalIcon;
    
    public RoundedButton() {
        this("");
    }
    
    public RoundedButton(String text) {
        super(text);
        // 关键：必须设置为不透明，否则矩形背景会露出来
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }
    
    
    /**
     * 设置是否进入加载状态
     *
     * @param loading true 则显示旋转图标并禁用点击，false 恢复原状
     */
    public void setLoading(boolean loading) {
        if (this.loading == loading || loadingIcon == null) {
            return;
        }
        this.loading = loading;
        
        if (loading) {
            super.setIcon(loadingIcon);
            loadingIcon.setRotating(true, this);
        } else {
            super.setIcon(originalIcon); // 恢复图标
            loadingIcon.setRotating(false, this);
        }
        repaint();
    }
    
    public RoundedButton setRadius(int radius) {
        this.radius = radius;
        return this;
    }
    
    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
        this.originalIcon = defaultIcon;
        if (loadingIcon != null) {
            loadingIcon.setRotating(false, this);
        }
        this.loadingIcon = new ScaledIcon(new ImageIcon("img/loading.png"), defaultIcon.getIconWidth(), defaultIcon.getIconHeight());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // 开启抗锯齿，确保圆角平滑
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制背景
        if (!getModel().isEnabled()) {
            g2.setColor(ColorUIManager.colorBackgroundGray);
        } else if (getModel().isArmed()) {
            g2.setColor(getBackground().darker()); // 点击时颜色加深
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().brighter()); // 悬停时颜色变亮
        } else {
            g2.setColor(getBackground());
        }
        
        // 填充圆角矩形
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
    
    public boolean isLoading() {
        return loading;
    }
}
