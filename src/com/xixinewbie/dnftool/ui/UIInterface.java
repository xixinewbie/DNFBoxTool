package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.*;
import com.xixinewbie.dnftool.model.Callback;
import com.xixinewbie.dnftool.model.Operation;
import com.xixinewbie.dnftool.model.Task;
import com.xixinewbie.dnftool.util.JsonUtil;
import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static com.xixinewbie.dnftool.manager.ColorUIManager.*;

public class UIInterface extends JFrame {
    
    private int margin = 12;
    private int margin2 = 8;
    private int margin3 = 6;
    
    private int wTitleBar = 500;
    private int hTitleBar = 38;
    private int xTitleBar = 0;
    private int yTitleBar = 0;
    
    private int wRootPanel = wTitleBar;
    private int xRootPanel = 0;
    private int yRootPanel = 0;
    
    private int wTaskPanel = wRootPanel;
    private int hTaskPanel = 440;
    private int xTaskPanel = 0;
    private int yTaskPanel = hTitleBar + 1;
    
    private int wSwitch = 40;
    private int hSwitch = 24;
    
    private int wOptions = wRootPanel;
    private int hOptions = 80;
    private int xOptions = 0;
    private int yOptions = yTaskPanel + hTaskPanel + 1;
    
    private int hRootPanel = yOptions + hOptions;
    
    private JPanel taskListView;
    private RoundedButton buttonAddTask;
    private RoundedButton buttonLoad;
    private SmoothSwitch jumpMoveCheckBox;
    private SmoothSwitch highSpeedCheckBox;
    private JLabel titleLabel;
    
    private ItemListener jumpMouseMoveListener;
    private ItemListener highSpeedListener;
    
    public UIInterface() {
        setUndecorated(true);
        super.setTitle("DNF开盒工具");
        initFonts(this);
        getContentPane().setBackground(null);
        
        setResizable(false);
        UIManager.put("Button.disabledBackground", colorButtonDisabled);
        ImageIcon icon = new ImageIcon("img/icon.png");
        setIconImage(icon.getImage());
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Listener.stop();
                System.exit(0);
            }
        });
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();
        
        int w_screen = dimension.width;
        int h_screen = dimension.height;
        
        JPanel rootPanel = initRootPanel();
        initTitleBar(rootPanel);
        initScroll(rootPanel);
        initButtonPanel(rootPanel);
        
        setAlwaysOnTop(true);
        pack();
        setLocation(w_screen - getWidth() - 10, h_screen - getHeight() - 60);
        this.setVisible(true);
        initUIData();
    }
    
    public void initTitleBar(JPanel rootPanel) {
        JPanel titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBounds(xTitleBar, yTitleBar, wTitleBar, hTitleBar);
        titleBar.setBackground(colorBackgroundTitleBar);
        
        titleLabel = new JLabel("DNF开盒工具");
        titleLabel.setForeground(colorTextTitleBar);
        ColorUIManager.setTextSize(titleBar, 12);
        titleLabel.setBounds(10, (hTitleBar - 30) / 2 - 2, (int) (wTitleBar * 0.6f), 30);
        titleBar.add(titleLabel);
        
        int wClose = (int) (hTitleBar * 1.2f);
        int hClose = hTitleBar;
        // --- 添加关闭按钮 (RoundedButton) ---
        RoundedButton closeButton = new RoundedButton();
        closeButton.setRadius(0);
        ColorUIManager.setDefaultIcon(closeButton, "close.png", 8);
        closeButton.setBounds(wTitleBar - wClose, 0, wClose, hClose);
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.setBackground(null);
        titleBar.add(closeButton);
        
        RoundedButton minButton = new RoundedButton();
        minButton.setRadius(0);
        ColorUIManager.setDefaultIcon(minButton, "min.png", 8);
        minButton.setBounds(wTitleBar - wClose * 2, 0, wClose, hClose);
        minButton.addActionListener(e -> UIInterface.this.setExtendedState(JFrame.ICONIFIED));
        minButton.setBackground(null);
        titleBar.add(minButton);
        
        rootPanel.add(titleBar);
        
        // --- 重要：添加拖拽逻辑 ---
        addWindowDragListener(titleBar);
    }
    
    private int mouseX, mouseY;
    
    private void addWindowDragListener(JPanel titleBar) {
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 计算位移并移动 JFrame
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
            }
        });
    }
    
    public JPanel initRootPanel() {
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(null);
        rootPanel.setPreferredSize(new Dimension(wRootPanel, hRootPanel));
        rootPanel.setLocation(xRootPanel, yRootPanel);
        rootPanel.setBackground(colorBackgroundItem);
        add(rootPanel);
        return rootPanel;
    }
    
    private void initScroll(JPanel rootPanel) {
        
        taskListView = new JPanel();
        taskListView.setLayout(new VerticalFlowLayout(0, 0, 0, true, false));
        taskListView.setBackground(colorBackground);
        
        JScrollPane jScrollPane = new JScrollPane(taskListView);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        jScrollPane.setBounds(margin2, 0, wTaskPanel - margin2 * 2, hTaskPanel);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ColorUIManager.setScrollBar(jScrollPane);
        jScrollPane.setBackground(colorBackground);
        JPanel background = new JPanel();
        background.setLayout(null);
        background.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        background.setBackground(colorBackground);
        background.setBounds(xTaskPanel, yTaskPanel, wTaskPanel, hTaskPanel);
        background.add(jScrollPane);
        rootPanel.add(background);
    }
    
    public void initButtonPanel(JPanel rootPanel) {
        JPanel container = new JPanel();
        container.setLayout(null);
        container.setBounds(xOptions, yOptions, wOptions, hOptions);
        container.setBackground(colorBackground);
        rootPanel.add(container);
        
        
        int wAddButton = 100;
        int hAddButton = 30;
        
        buttonAddTask = new RoundedButton("创建新脚本");
        buttonAddTask.setBackground(colorAddTask);
        buttonAddTask.setMargin(new Insets(0, 0, 0, 0));
        buttonAddTask.setBorder(null);
        buttonAddTask.setFocusable(false);
        int xAddTask = margin;
        int yAddTask = margin;
        buttonAddTask.setBounds(xAddTask, yAddTask, wAddButton, hAddButton);
        ColorUIManager.setDefaultIcon(buttonAddTask, "add.png");
        ColorUIManager.setTextColor(buttonAddTask, colorTextDark);
        container.add(buttonAddTask);
        
        buttonAddTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ActionManager.addTask(
                        new Task().setCreateTime(S.now())
                                .setWindowSize(WindowPositionManager.wGameWindow, WindowPositionManager.hGameWindow)
                                .setCount(1)
                );
                StorageManager.saveToFile();
                reloadList();
                flushGlobalUI();
            }
        });
        
        int wButton = 80;
        int hButton = 30;
        
        buttonLoad = new RoundedButton("加载");
        buttonLoad.setBackground(colorLoad);
        buttonLoad.setMargin(new Insets(0, 0, 0, 0));
        buttonLoad.setBorder(null);
        buttonLoad.setFocusable(false);
        ColorUIManager.setTextColor(buttonLoad, colorTextDark);
        buttonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 使用 java.awt.FileDialog 调用原生打开窗口
                FileDialog fileDialog = new FileDialog(UIInterface.this, "选择脚本", FileDialog.LOAD);
                fileDialog.setDirectory(".");
                // Windows 下可以设置过滤器提示，但 FileDialog 的过滤器通常通过 setFile 设置初始匹配
                fileDialog.setFile("*" + StorageManager.FILE_EXTENSION);
                UIInterface.this.setAlwaysOnTop(false);
                fileDialog.setVisible(true);
                UIInterface.this.setAlwaysOnTop(true);
                fileDialog.setAlwaysOnTop(true);
                
                String directory = fileDialog.getDirectory();
                String fileName = fileDialog.getFile();
                
                if (directory != null && fileName != null) {
                    File file = new File(directory, fileName);
//                    S.s("load from :" + file.getAbsolutePath());
                    String json = StorageManager.read(file.getAbsolutePath());
                    
                    Task newTask = JsonUtil.toTask(json);
                    if (newTask != null) {
                        newTask.setCreateTime(S.now());
                        ActionManager.addTask(newTask);
                        StorageManager.saveToFile();
                        reloadList();
                        flushGlobalUI();
                    }
                }
            }
        });
        int xLoad = xAddTask + wAddButton + margin;
        int yLoad = margin;
        int wLoad = wButton;
        int hLoad = hButton;
        buttonLoad.setBounds(xLoad, yLoad, wLoad, hLoad);
        container.add(buttonLoad);
        
        jumpMouseMoveListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ActionManager.ignoreMove = jumpMoveCheckBox.isSelected();
                StorageManager.saveToFile();
            }
        };
        highSpeedListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ActionManager.highSpeed = highSpeedCheckBox.isSelected();
                StorageManager.saveToFile();
            }
        };
        
        
        int xJumpMoveLabel = xLoad + wAddButton + margin3;
        int yJumpMoveLabel = margin;
        int wJumpMoveLabel = wButton;
        int hJumpMoveLabel = hButton;
        JLabel jumpMoveLabel = new JLabel("跳过鼠标移动");
        jumpMoveLabel.setBounds(xJumpMoveLabel, yJumpMoveLabel, wJumpMoveLabel, hJumpMoveLabel);
        jumpMoveLabel.setOpaque(true);
        jumpMoveLabel.setForeground(colorTextSwitchTitle);
        jumpMoveLabel.setBackground(ColorUIManager.colorSwitchTitleBackground);
        jumpMoveLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        jumpMoveLabel.setVerticalAlignment(SwingConstants.CENTER);
        container.add(jumpMoveLabel);
        
        int xJumpMove = xJumpMoveLabel + wJumpMoveLabel + 8;
        int yJumpMove = yJumpMoveLabel + 3;
        int wJumpMove = wSwitch;
        int hJumpMove = hSwitch;
        jumpMoveCheckBox = new SmoothSwitch();
        jumpMoveCheckBox.setSelected(ActionManager.ignoreMove);
        jumpMoveCheckBox.setBounds(xJumpMove, yJumpMove, wJumpMove, hJumpMove);
        container.add(jumpMoveCheckBox);
        
        
        int xHighSpeedLabel = xJumpMove + wJumpMove + margin3;
        int yHighSpeedLabel = margin;
        int wHighSpeedLabel = wButton;
        int hHighSpeedLabel = hButton;
        JLabel highSpeedLabel = new JLabel("点击低延迟");
        highSpeedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        highSpeedLabel.setVerticalAlignment(SwingConstants.CENTER);
        highSpeedLabel.setOpaque(true);
        highSpeedLabel.setBackground(ColorUIManager.colorSwitchTitleBackground);
        highSpeedLabel.setForeground(colorTextSwitchTitle);
        highSpeedLabel.setBounds(xHighSpeedLabel, yHighSpeedLabel, wHighSpeedLabel, hHighSpeedLabel);
        container.add(highSpeedLabel);
        
        int xHighSpeed = xHighSpeedLabel + wHighSpeedLabel + 8;
        int yHighSpeed = yHighSpeedLabel + 3;
        int wHighSpeed = wSwitch;
        int hHighSpeed = hSwitch;
        highSpeedCheckBox = new SmoothSwitch();
        highSpeedCheckBox.setSelected(ActionManager.highSpeed);
        highSpeedCheckBox.setBounds(xHighSpeed, yHighSpeed, wHighSpeed, hHighSpeed);
        container.add(highSpeedCheckBox);
    }
    
    private void getChildItem(final Task task, JPanel taskListView) {
        if (task == null) {
            return;
        }
        final ItemPanel item = new ItemPanel();
        int wItem = wTaskPanel - margin2 * 2;
        int hItem = 80;
        item.setPreferredSize(new Dimension(wItem, hItem));
        item.setLayout(null);
        
        int wIcon = (int) (hItem * 0.7f);
        int hIcon = wIcon;
        int xIcon = margin;
        int yIcon = (hItem - hIcon) / 2;
        
        int wTaskName = 280;
        int hTaskName = 30;
        int xTaskName = xIcon + wIcon + margin2;
        int yTaskName = yIcon - 5;
        //
        int wPlay = 70;
        int hPlay = 30;
        int xPlay = wItem - wPlay - 15;
        int yPlay = hItem - margin - hPlay;
        //
        int wEdit = 70;
        int hEdit = 30;
        int xEdit = xPlay - wEdit - margin2;
        int yEdit = yPlay;
        
        int wCountTitle = 65;
        int hCountTitle = 30;
        int xCountTitle = xTaskName;
        int yCountTitle = yTaskName + hTaskName;
        
        int wCount = 40;
        int hCount = 30;
        int xCount = xCountTitle + wCountTitle;
        int yCount = yCountTitle + 2;
        
        int wCreateTime = wItem - wTaskName;
        int hCreateTime = 20;
        int xCreateTime = wItem - wCreateTime - 15;
        int yCreateTime = yTaskName + 2;
        
        RoundedButton iconView = new RoundedButton();
        iconView.setMargin(new Insets(0, 0, 0, 0));
        iconView.setBounds(xIcon, yIcon, wIcon, hIcon);
        iconView.setBackground(null);
        ColorUIManager.setIcon(iconView, task.getIcon(), iconView.getWidth());
        //
        JTextField taskNameView = new JTextField();
        taskNameView.setSize(wTaskName, hTaskName);
        ColorUIManager.setTextSize(taskNameView, textSizeTaskName);
        taskNameView.setLocation(xTaskName, yTaskName);
        taskNameView.setBackground(colorTaskNameBackground);
        ColorUIManager.setTextColor(taskNameView, colorText);
        ColorUIManager.setField(taskNameView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                task.setName(s);
                StorageManager.saveToFile();
            }
        });
        
        //
        JLabel countTitleView = new JLabel("执行次数:");
        countTitleView.setSize(wCountTitle, hCountTitle);
        countTitleView.setLocation(xCountTitle, yCountTitle);
        countTitleView.setOpaque(true);
        countTitleView.setHorizontalAlignment(SwingConstants.LEFT);
        countTitleView.setVerticalAlignment(SwingConstants.CENTER);
        countTitleView.setBackground(colorTaskNameBackground);
        ColorUIManager.setTextSize(countTitleView, textSizeButton);
        ColorUIManager.setTextColor(countTitleView, colorText);
        //
        final JTextField countView = new JTextField(String.valueOf(task.getCount()));
        countView.setSize(wCount, hCount);
        countView.setLocation(xCount, yCount);
        countView.setHorizontalAlignment(JTextField.CENTER);
        countView.setBackground(ColorUIManager.colorCount);
        ColorUIManager.setField(countView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                task.setCount(S.intValue(countView.getText()));
                StorageManager.saveToFile();
            }
        });
        ColorUIManager.setTextSize(countView, textSizeButton);
        ColorUIManager.setTextColor(countView, colorText);
        ((PlainDocument) countView.getDocument()).setDocumentFilter(new NumberInputFilter(999));
        //
        final JButton buttonEdit = new RoundedButton();
        ColorUIManager.setDefaultIcon(buttonEdit, "edit.png");
        ColorUIManager.setTextColor(buttonEdit, colorTextLight);
        buttonEdit.setMargin(new Insets(0, 0, 0, 0));
        buttonEdit.setText("编辑");
        buttonEdit.setBackground(colorBackgroundEdit);
        buttonEdit.setBorder(null);
        buttonEdit.setFocusable(false);
        buttonEdit.setSize(wEdit, hEdit);
        buttonEdit.setLocation(xEdit, yEdit);
        
        //
        final RoundedButton buttonPlay = new RoundedButton();
        buttonPlay.setText("执行");
        ColorUIManager.setDefaultIcon(buttonPlay, "play.png");
        ColorUIManager.setTextColor(buttonPlay, colorTextLight);
        buttonPlay.setBackground(colorBackgroundPlay);
        buttonPlay.setSize(wPlay, hPlay);
        buttonPlay.setBorder(null);
        buttonPlay.setFocusable(false);
        buttonPlay.setMargin(new Insets(0, 0, 0, 0));
        buttonPlay.setLocation(xPlay, yPlay);
        buttonPlay.setBorderPainted(false);
        buttonPlay.setFocusPainted(false);
        
        item.setFlushUI(new ItemPanel.UIFlusher() {
            @Override
            public void onFlushUI() {
                taskNameView.setText(getName(task));
                taskNameView.setCaretPosition(0);
                ColorUIManager.setIcon(iconView, task.getIcon(), iconView.getWidth());
                
                countView.setText(String.valueOf(task.getCount()));
                countView.setCaretPosition(0);
                
                Task playingTask = PlayManager.isPlaying() ? PlayManager.getPlayingTask() : null;
                boolean isPlaying = PlayManager.isPlaying();
                boolean isRecording = RecordManager.isRecording();
                boolean enable = !isPlaying && !isRecording;
                boolean isCurrentPlaying = playingTask != null && playingTask.getCreateTime() == task.getCreateTime();
                boolean gameExists = WindowPositionManager.gameWindowExists;
                
                buttonPlay.setEnabled(gameExists && !isPlaying && !isRecording && !task.isEnpty());
                buttonPlay.setLoading(isCurrentPlaying);
                taskNameView.setEnabled(enable);
                countView.setEnabled(enable);
                buttonEdit.setEnabled(enable);
                
                item.setBackground(isCurrentPlaying ? colorBackgroundItemPlaying : colorBackgroundItem);
                
            }
        });
        buttonPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PlayManager.startPlay(task, null, new TaskExecutor.Callback() {
                    @Override
                    public void onRun(Operation operation, int countOperation, int count) {
                        setTitle("[ " + count + "/" + task.getCount() + " ] " + getName(operation) + " " + countOperation + "/" + operation.getCount());
                        flushGlobalUI();
                    }
                    
                    @Override
                    public void onEnd() {
                        setTitle("执行完毕");
                        flushGlobalUI();
                    }
                    
                    @Override
                    public void onBreak(String quitMsg) {
                        setTitle("执行中断：" + quitMsg);
                        flushGlobalUI();
                    }
                });
                flushGlobalUI();
            }
        });
        buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TaskEditDialog dialog = new TaskEditDialog(UIInterface.this, task);
                dialog.setVisible(true); // This blocks until the dialog is closed
                
                if (!ActionManager.tasks.contains(task)) {
                    taskListView.remove(item);
                    taskListView.revalidate();
                    taskListView.repaint();
                }
                flushGlobalUI();
            }
        });
        
        JLabel createTimeView = new JLabel(S.getTime(task.getCreateTime()));
        createTimeView.setSize(wCreateTime, hCreateTime);
        createTimeView.setLocation(xCreateTime, yCreateTime);
        createTimeView.setOpaque(true);
        createTimeView.setHorizontalAlignment(SwingConstants.RIGHT);
        createTimeView.setVerticalAlignment(SwingConstants.CENTER);
        createTimeView.setBackground(null);
        ColorUIManager.setTextSize(createTimeView, 12);
        ColorUIManager.setTextColor(createTimeView, colorText);
        
        
        addItemMouseColor(task, item, buttonEdit, item, taskNameView, countView, buttonPlay);
        
        item.add(iconView);
        item.add(taskNameView);
        item.add(countView);
        item.add(buttonEdit);
        item.add(buttonPlay);
        item.add(countTitleView);
        item.add(createTimeView);
        
        taskListView.add(item);
        
        item.flushUI();
    }
    
    private void addItemMouseColor(Task task, JPanel item, JComponent... components) {
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            
            }
            
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            
            }
            
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            
            }
            
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (!PlayManager.isPlaying() && !RecordManager.isRecording()) {
                    item.setBackground(colorBackgroundItemSelected);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (!PlayManager.isPlaying() && !RecordManager.isRecording()) {
                    item.setBackground(colorBackgroundItem);
                }
            }
        };
        for (JComponent component : components) {
            component.addMouseListener(mouseListener);
        }
    }
    
    private void initUIData() {
        StorageManager.loadAllSprites();
        WindowPositionManager.detector = new WindowPositionManager.WindowDetector() {
            @Override
            public void onWindowChange(boolean exists) {
                if (!exists) {
                    setTitle("未检测到游戏窗口");
                } else {
                    setTitle("检测到游戏窗口");
                }
                flushGlobalUI();
            }
        };
        WindowPositionManager.init();
        
        reloadList();
        flushGlobalUI();
    }
    
    private void flushGlobalUI() {
        boolean enable = !PlayManager.isPlaying() && !RecordManager.isRecording();
        
        jumpMoveCheckBox.removeItemListener(jumpMouseMoveListener);
        jumpMoveCheckBox.setSelected(ActionManager.ignoreMove);
        jumpMoveCheckBox.addItemListener(jumpMouseMoveListener);
        jumpMoveCheckBox.setEnabled(enable);
        
        highSpeedCheckBox.removeItemListener(highSpeedListener);
        highSpeedCheckBox.setSelected(ActionManager.highSpeed);
        highSpeedCheckBox.addItemListener(highSpeedListener);
        highSpeedCheckBox.setEnabled(enable);
        
        buttonAddTask.setEnabled(enable);
        buttonLoad.setEnabled(enable);
        
        flushList();
    }
    
    private void flushList() {
        int childCount = taskListView.getComponentCount();
        for (int i = 0; i < childCount; i++) {
            Component component = taskListView.getComponent(i);
            if (component instanceof ItemPanel itemPanel) {
                itemPanel.flushUI();
            }
        }
    }
    
    private void reloadList() {
        taskListView.removeAll();
        List<Task> tasks = ActionManager.tasks;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            getChildItem(task, taskListView);
        }
        taskListView.revalidate();
        taskListView.repaint();
    }
    
    @Override
    public void setTitle(String title) {
//        S.s(title);
        titleLabel.setText("DNF开盒工具   " + title);
    }
    
    public String getName(Task task) {
        return S.isEmpty(task.getName()) ? "脚本" + task.getCreateTime() : task.getName();
    }
    
    public String getName(Operation operation) {
        return S.isEmpty(operation.getName()) ? "任务" + operation.getCreateTime() : operation.getName();
    }
    
    /*
        使用jlink来编译一个缩减版JRE
        jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging --output custom-jre
     */
    public static void main(String[] args) {
        Logger logger;
        UIInterface mapDrawer = new UIInterface();
        Listener.start();
    }
}
