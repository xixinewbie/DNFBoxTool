package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.ColorUIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ConfirmDialog extends JDialog {
    private boolean confirmed = false;
    private int mouseX, mouseY;
    
    public ConfirmDialog(Frame owner, Component parentComponent, String message) {
        super(owner, true);
        setUndecorated(true); // 隐藏默认标题栏
        setBackground(new Color(0, 0, 0, 0)); // 背景透明以支持圆角
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ColorUIManager.colorBackgroundDialog); // 对话框背景色
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                
                // 绘制一个浅色边框
                g2.setColor(ColorUIManager.colorBackgroundDark);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(300, 120));
        
//        // 自定义标题文字
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
//        titleLabel.setForeground(ColorUIManager.colorTextLight);
//        titleLabel.setBounds(20, 15, 200, 25);
//        mainPanel.add(titleLabel);
        
        // 提示内容
        JLabel messageLabel = new JLabel("<html><div style='width:220px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        messageLabel.setForeground(ColorUIManager.colorTextLight);
        messageLabel.setBounds(25, 20, 250, 50);
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        mainPanel.add(messageLabel);
        
        // 按钮容器
        int btnW = 80, btnH = 30;
        
        // 取消按钮
        RoundedButton cancelBtn = new RoundedButton("取消");
        cancelBtn.setBackground(ColorUIManager.colorBackgroundLight);
        cancelBtn.setForeground(ColorUIManager.colorTextLight);
        cancelBtn.setBounds(200, 80, btnW, btnH);
        cancelBtn.addActionListener(e -> dispose());
        mainPanel.add(cancelBtn);
        
        // 确定按钮
        RoundedButton okBtn = new RoundedButton("确定");
        okBtn.setBackground(ColorUIManager.colorBackgroundLight);
        okBtn.setForeground(ColorUIManager.colorTextLight);
        okBtn.setBounds(110, 80, btnW, btnH);
        okBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        mainPanel.add(okBtn);
        
        // 添加拖拽逻辑
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        mainPanel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });
        
        add(mainPanel);
        pack();
        // --- 关键修改：根据 parentComponent 计算位置 ---
        if (parentComponent != null) {
            Point p = parentComponent.getLocationOnScreen(); // 获取组件在屏幕上的绝对位置
            int dialogX = p.x + (parentComponent.getWidth() / 2) - (getWidth() / 2); // 对话框水平居中于组件
            int dialogY = p.y + parentComponent.getHeight() + 5; // 对话框显示在组件下方 5px
            
            // 确保对话框不会超出屏幕边界
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenRect = ge.getMaximumWindowBounds(); // 获取屏幕可用区域
            
            if (dialogX < screenRect.x) dialogX = screenRect.x;
            if (dialogY < screenRect.y) dialogY = screenRect.y;
            if (dialogX + getWidth() > screenRect.x + screenRect.width) {
                dialogX = screenRect.x + screenRect.width - getWidth();
            }
            if (dialogY + getHeight() > screenRect.y + screenRect.height) {
                dialogY = screenRect.y + screenRect.height - getHeight();
            }
            
            setLocation(dialogX, dialogY);
        } else {
            setLocationRelativeTo(owner); // 如果没有指定组件，则居中于父窗口
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    // 静态快捷调用方法
    public static boolean show(Frame owner, Component parentComponent, String message) {
        ConfirmDialog dialog = new ConfirmDialog(owner, parentComponent, message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}
