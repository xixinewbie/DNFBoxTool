package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class ScaledIcon implements Icon {
    private final Image image;
    private final int width;
    private final int height;
    private final int imgWidth;
    private final int imgHeight;
    
    // 旋转相关
    private boolean rotating = false;
    private float angle = 0;
    private Timer timer;
    private Component parent; // 需要重绘的组件
    
    /**
     * @param icon   原始图标（不被修改）
     * @param width  希望显示的宽度
     * @param height 希望显示的高度
     */
    public ScaledIcon(ImageIcon icon, int width, int height) {
        this.image = icon.getImage();
        this.imgWidth = icon.getIconWidth();
        this.imgHeight = icon.getIconHeight();
        this.width = width;
        this.height = height;
    }
    
    /**
     * 开启或关闭旋转动画
     *
     * @param rotating 是否旋转
     * @param parent   承载该图标的组件（用于触发重绘）
     */
    public void setRotating(boolean rotating, Component parent) {
        this.rotating = rotating;
        this.parent = parent;
        
        if (rotating) {
            if (timer == null) {
                timer = new Timer(16, e -> {
                    angle += 10;
                    if (angle >= 360) angle = 0;
                    if (this.parent != null) {
                        this.parent.repaint();
                    }
                });
            }
            timer.start();
        } else {
            if (timer != null) {
                timer.stop();
            }
            angle = 0;
        }
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        // 开启抗锯齿和平滑渲染，确保缩小显示时依然清晰
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int wDraw, hDraw, xDraw, yDraw;
        if (height < c.getHeight() && width < c.getWidth()) {
            wDraw = width;
            hDraw = height;
            xDraw = x;
            yDraw = y;
        } else {
            float ratioImg = imgWidth / (float) imgHeight;
            float ratio = width / (float) height;
            if (ratioImg > ratio) {
                wDraw = width;
                hDraw = (int) (width / ratioImg);
                xDraw = x;
                yDraw = (height - hDraw) / 2;
            } else {
                wDraw = (int) (height * ratioImg);
                hDraw = height;
                xDraw = (width - wDraw) / 2;
                yDraw = y;
            }
        }
        if (rotating) {
            // 执行旋转变换
            // 旋转中心点是图标的正中心：x + width/2, y + height/2
            AffineTransform at = g2d.getTransform();
            g2d.rotate(Math.toRadians(angle), x + width / 2.0, y + height / 2.0);
            g2d.drawImage(image, xDraw, yDraw, wDraw, hDraw, c);
            g2d.setTransform(at);
        } else {
            g2d.drawImage(image, xDraw, yDraw, wDraw, hDraw, c);
        }
        
        g2d.dispose();
    }
    
    @Override
    public int getIconWidth() {
        return width;
    }
    
    @Override
    public int getIconHeight() {
        return height;
    }
}