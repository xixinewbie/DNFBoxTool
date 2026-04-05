package com.xixinewbie.dnftool.manager;

import com.xixinewbie.dnftool.model.Callback;
import com.xixinewbie.dnftool.ui.ModernScrollBarUI;
import com.xixinewbie.dnftool.ui.ScaledIcon;
import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import static com.xixinewbie.dnftool.manager.StorageManager.IMG_PATH;

/**
 * created by zhaoyuntao
 * on 2026/04/04
 */
public class ColorUIManager {
    public static int textSizeButton = 14;
    public static int textSizeTaskName = 16;
    public static Color colorTaskNameBackground = S.debugColor ? Color.decode("#00ff00") : null;
    public static Color colorBackgroundRecord = Color.decode("#2e436e");
    public static Color colorBackgroundEdit = Color.decode("#2e436e");
    public static Color colorBackgroundPlay = Color.decode("#275c2c");
    public static Color colorBackgroundDelete = Color.decode("#a92f2f");
    public static Color colorBackgroundExport = Color.decode("#aa7722");
    public static Color colorBackgroundImport = Color.decode("#2e436e");
    public static Color colorBackgroundDialog = Color.decode("#2b2d30");
    public static Color colorTextLight = Color.decode("#efefef");
    public static Color colorText = Color.decode("#8089a3");
    public static Color colorTextRed = Color.decode("#f38980");
    public static Color colorTextDark = Color.decode("#2b2d30");
    public static Color colorAddTask = Color.decode("#57965c");
    public static Color colorAddSleepTask = Color.decode("#d19275");
    public static Color colorSave = Color.decode("#ffaa66");
    public static Color colorLoad = Color.decode("#aa9657");
    public static Color colorBackgroundTitleBar = Color.decode("#2b2d30");
    public static Color colorBackground = Color.decode("#2b2d30");
    public static Color colorBackgroundLight = Color.decode("#3a3a3a");
    public static Color colorBackgroundLight2 = Color.decode("#3b3d40");
    public static Color colorBackgroundDark = Color.decode("#1e1f22");
    public static Color colorBackgroundItem = Color.decode("#1e1f22");
    public static Color colorBackgroundItemSleep = Color.decode("#202028");
    public static Color colorBackgroundItemPlaying = Color.decode("#2e2f45");
    public static Color colorBackgroundItemSelected = Color.decode("#26282e");
    public static Color colorButtonDisabled = Color.decode("#efefef");
    public static Color colorCount = Color.decode("#f8f8fa");
    public static Color colorSwitch = Color.decode("#2e436e");
    public static Color colorSwitchDisabled = Color.decode("#202020");
    public static Color colorSwitchOff = Color.decode("#909090");
    public static Color colorSwitchTitleBackground = S.debugColor ? Color.decode("#f890f8") : new Color(0, 0, 0, 0);
    public static Color colorTextSwitchTitle = Color.decode("#75787b");
    public static Color colorScrollBar = new Color(255, 255, 255, 30);
    public static Color colorTextTitleBar = Color.decode("#c0c0c0");
    public static Color colorBackgroundGray=Color.decode("#3f3f3f");
    
    public static void setDefaultIcon(JButton jButton, String image) {
        setDefaultIcon(jButton, image, 16);
    }
    
    public static void setDefaultIcon(JButton jButton, String image, int size) {
        if (!image.startsWith(IMG_PATH)) {
            image = IMG_PATH + image;
        }
        setIcon(jButton, image, size);
    }
    
    public static void setIcon(JButton jButton, String image, int size) {
        if (S.isEmpty(image) || !new File(image).exists()) {
            image = IMG_PATH + "unknown.png";
        }
        jButton.setVerticalAlignment(SwingConstants.CENTER);
        jButton.setIconTextGap(5);
        jButton.setIcon(new ScaledIcon(new ImageIcon(image), size, size));
    }
    
    public static void setTextSize(JComponent jComponent, int textSize) {
        jComponent.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, textSize));
    }
    
    public static void setTextColor(JComponent jComponent, Color colorText) {
        jComponent.setForeground(colorText);
    }
    
    public static void setScrollBar(JScrollPane jScrollPane) {
        // --- 关键代码：应用自定义滚动条 UI ---
        jScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        jScrollPane.getVerticalScrollBar().setUnitIncrement(2); // 建议设为 16 或更大，比如 20
        // 额外建议：去除 JScrollPane 的默认边框，看起来更高级
        jScrollPane.setBorder(null);
    }
    
    public static void setField(JTextField jTextField, Callback<String> callback) {
        jTextField.setOpaque(false);           // 平时背景透明
        jTextField.setBorder(null);            // 平时无边框
        
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // --- 进入编辑状态 ---
                jTextField.setOpaque(true);
                jTextField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 显示边框
                jTextField.selectAll(); // 自动全选，方便用户直接覆盖输入
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // --- 退出编辑状态 ---
                jTextField.setCaretPosition(0);
                jTextField.setOpaque(false);
                jTextField.setBorder(null);
                // 在这里保存数据到你的模型中
                String newName = jTextField.getText();
                callback.onResult(newName);
            }
        });
        jTextField.addActionListener(e -> {
            // 按回车时，手动让父容器获取焦点，从而触发 FocusLost
            jTextField.getParent().requestFocusInWindow();
        });
    }
    
    public static void initFonts(JFrame frame) {
        // 设置全局字体
        Font globalFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        UIManager.put("Button.font", globalFont);
        UIManager.put("CheckBox.font", globalFont);
        UIManager.put("RadioButton.font", globalFont);
        UIManager.put("ToggleButton.font", globalFont);
        UIManager.put("ComboBox.font", globalFont);
        UIManager.put("TabbedPane.font", globalFont);
        UIManager.put("MenuBar.font", globalFont);
        UIManager.put("Menu.font", globalFont);
        UIManager.put("MenuItem.font", globalFont);
        UIManager.put("PopupMenu.font", globalFont);
        UIManager.put("OptionPane.font", globalFont);
        UIManager.put("ProgressBar.font", globalFont);
        UIManager.put("ScrollPane.font", globalFont);
        UIManager.put("Viewport.font", globalFont);
        UIManager.put("TableHeader.font", globalFont);
        UIManager.put("Table.font", globalFont);
        UIManager.put("TextField.font", globalFont);
        UIManager.put("TextArea.font", globalFont);
        UIManager.put("PasswordField.font", globalFont);
        UIManager.put("EditorPane.font", globalFont);
        UIManager.put("FormattedTextField.font", globalFont);
        UIManager.put("Spinner.font", globalFont);
        UIManager.put("Label.font", globalFont);
        UIManager.put("List.font", globalFont);
        UIManager.put("Tree.font", globalFont);
        // 应用全局字体
        SwingUtilities.updateComponentTreeUI(frame);
        
        // 设置全局提示框的背景色和文字颜色
        UIManager.put("ToolTip.background", Color.decode("#F0F4F8")); // 使用你的冷色调背景
        UIManager.put("ToolTip.foreground", Color.decode("#334E68")); // 使用深石板蓝文字
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Color.decode("#D9E2EC"))); // 边框
    }
}
