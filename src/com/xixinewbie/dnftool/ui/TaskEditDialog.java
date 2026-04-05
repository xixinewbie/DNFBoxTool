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

import static com.xixinewbie.dnftool.manager.ColorUIManager.*;

public class TaskEditDialog extends JDialog {
    private int margin = 12;
    private int margin2 = 8;
    private int wButton = 70;
    private int hButton = 30;
    
    private int wTitleBar = 700;
    private int hTitleBar = 32;
    private int xTitleBar = 0;
    private int yTitleBar = 0;
    
    private int wRootPanel = wTitleBar;
    private int xRootPanel = 0;
    private int yRootPanel = 0;
    
    private int wTopPanel = wRootPanel;
    private int hTopPanel = 100;
    private int xTopPanel = 0;
    private int yTopPanel = yTitleBar + hTitleBar + 1;
    
    private int wTaskPanel = wRootPanel;
    private int hTaskPanel = 350;
    private int xTaskPanel = 0;
    private int yTaskPanel = yTopPanel + hTopPanel + 1;
    private int wItem = wTaskPanel - 15;
    private int hItem = 60;
    private int yButton = (hItem - hButton) / 2;
    private int xDelete = wItem - wButton - 18;
    private int wTaskName = 250;
    private int wCount = 60;
    private int wCountTitle = 80;
    private int xTaskName = margin;
    private int yTaskName = margin;
    private int xPlay = xDelete - wButton - margin2;
    private int xRecord = xPlay - wButton - margin2;
    private int xCount = xRecord - wCount - margin2;
    private int xCountTitle = xCount - wCountTitle;
    
    private int wOptions = wRootPanel;
    private int hOptions = 180;
    private int xOptions = 0;
    private int yOptions = yTaskPanel + hTaskPanel + 1;
    
    private int hRootPanel = yOptions + hOptions;
    private final int wDialog = wTitleBar;
    private final int hDialog = hRootPanel;
    private final Frame owner;
    
    private JLabel titleLabel;
    
    private JLabel addPositionTitleView;
    private JTextField addPositionView;
    
    private JLabel taskNameTitleView;
    private JTextField taskNameView;
    
    private JTextField taskCountView;
    private RoundedButton testButton;
    
    private RoundedButton buttonAddSleep;
    private RoundedButton buttonAddOpration;
    private RoundedButton buttonimport;
    private RoundedButton buttonExport;
    private RoundedButton buttonDelete;
    
    private JPanel operationListView;
    
    private boolean confirmed = false;
    private Task task;
    private static final long DEFAULT_SLEEP_TIME = 200;
    
    public TaskEditDialog(Frame owner, Task task) {
        super(owner, "编辑", true); // true for modal
        this.owner = owner;
        this.task = task;
        setResizable(false);
        setUndecorated(true);
        
        setLayout(null);
        JPanel rootPanel = new JPanel();
        rootPanel.setBackground(colorBackgroundDark);
        rootPanel.setLayout(null);
        rootPanel.setBounds(xRootPanel, yRootPanel, wDialog, hDialog);
        add(rootPanel);
        initTitleBar(rootPanel);
        initTopPanel(rootPanel);
        initScroll(rootPanel);
        initButtonPanel(rootPanel);
        
        // Set a preferred size for the dialog
        setPreferredSize(new Dimension(wDialog, hDialog));
        
        pack();
        
        // --- 关键修改：根据 parentComponent 计算位置 ---
        if (owner != null) {
            Point p = owner.getLocationOnScreen(); // 获取组件在屏幕上的绝对位置
            int dialogX = p.x + (owner.getWidth() / 2) - (getWidth() / 2); // 对话框水平居中于组件
            int dialogY = p.y + owner.getHeight() + 5; // 对话框显示在组件下方 5px
            
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
        
        reloadList();
        flushGlobalUI();
    }
    
    public void initTitleBar(JPanel rootPanel) {
        JPanel titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBounds(xTitleBar, yTitleBar, wTitleBar, hTitleBar);
        titleBar.setBackground(colorBackgroundTitleBar);
        titleLabel = new JLabel("编辑");
        titleLabel.setForeground(colorTextTitleBar);
        titleLabel.setBounds(10, 0, (int) (wTitleBar * 0.8f), 30);
        titleBar.add(titleLabel);
        
        int wClose = (int) (hTitleBar * 1.2f);
        int hClose = hTitleBar;
        // --- 添加关闭按钮 (RoundedButton) ---
        RoundedButton closeButton = new RoundedButton();
        closeButton.setRadius(0);
        ColorUIManager.setDefaultIcon(closeButton, "close.png", 8);
        closeButton.setBounds(wTitleBar - wClose, 0, wClose, hClose);
        closeButton.addActionListener(e -> dispose());
        closeButton.setBackground(null);
        titleBar.add(closeButton);
        
        rootPanel.add(titleBar);
        
        // --- 重要：添加拖拽逻辑 ---
        addWindowDragListener(titleBar);
    }
    
    public void initTopPanel(JPanel rootPanel) {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBounds(xTopPanel, yTopPanel, wTopPanel, hTopPanel);
        topPanel.setBackground(colorBackground);
        
        int wIcon = 60;
        int hIcon = 60;
        int xIcon = margin;
        int yIcon = margin;
        RoundedButton iconView = new RoundedButton();
        iconView.setMargin(new Insets(0, 0, 0, 0));
        iconView.setBounds(xIcon, yIcon, wIcon, hIcon);
        iconView.setBackground(null);
        ColorUIManager.setIcon(iconView, task.getIcon(), iconView.getWidth());
        iconView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 使用 java.awt.FileDialog 调用原生打开窗口
                FileDialog fileDialog = new FileDialog(owner, "选择图片", FileDialog.LOAD);
                fileDialog.setDirectory(".");
                fileDialog.setFile("*.png");
                owner.setAlwaysOnTop(false);
                fileDialog.setVisible(true);
                owner.setAlwaysOnTop(true);
                fileDialog.setAlwaysOnTop(true);
                
                String directory = fileDialog.getDirectory();
                String fileName = fileDialog.getFile();
                
                if (directory != null && fileName != null) {
                    File file = new File(directory, fileName);
                    task.setIcon(file.getAbsolutePath());
                    ColorUIManager.setIcon(iconView, file.getAbsolutePath(), iconView.getWidth());
                    StorageManager.saveToFile();
                }
            }
        });
        topPanel.add(iconView);
        
        int wTaskNameTitle = 45;
        int hTaskNameTitle = 30;
        int xTaskNameTitle = xIcon + wIcon + margin;
        int yTaskNameTitle = margin;
        taskNameTitleView = new JLabel("名称：");
        taskNameTitleView.setBounds(xTaskNameTitle, yTaskNameTitle, wTaskNameTitle, hTaskNameTitle);
        taskNameTitleView.setForeground(colorTextSwitchTitle);
        ColorUIManager.setTextColor(taskNameTitleView, colorTextLight);
        ColorUIManager.setTextSize(taskNameTitleView, textSizeButton);
        topPanel.add(taskNameTitleView);
        
        int xName = xTaskNameTitle + wTaskNameTitle + margin2;
        int yName = margin;
        int wName = 260;
        int hName = 30;
        //
        taskNameView = new JTextField(getName(task));
        taskNameView.setBounds(xName, yName + 2, wName, hName);
        taskNameView.setHorizontalAlignment(JTextField.LEFT);
        taskNameView.setBackground(colorBackgroundLight2);
        taskNameView.setForeground(colorTextLight);
        ColorUIManager.setTextColor(taskNameView, colorTextLight);
        ColorUIManager.setTextSize(taskNameView, textSizeButton);
        ColorUIManager.setField(taskNameView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                task.setName(s);
                StorageManager.saveToFile();
            }
        });
        
        topPanel.add(taskNameView);
        
        buttonimport = new RoundedButton("导入");
        buttonimport.setMargin(new Insets(0, 0, 0, 0));
        buttonimport.setBackground(colorBackgroundImport);
        ColorUIManager.setTextSize(buttonimport, textSizeButton);
        ColorUIManager.setTextColor(buttonimport, colorTextLight);
        ColorUIManager.setDefaultIcon(buttonimport, "import.png", 20);
        buttonimport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                FileDialog fileDialog = new FileDialog(owner, "导入", FileDialog.LOAD);
                fileDialog.setDirectory(".");
                String fileName = getName(task) + StorageManager.FILE_EXTENSION;
                fileDialog.setFile(fileName);
                owner.setAlwaysOnTop(false);
                fileDialog.setVisible(true);
                owner.setAlwaysOnTop(true);
                fileDialog.setAlwaysOnTop(true);
                
                String directory = fileDialog.getDirectory();
                fileName = fileDialog.getFile();
                
                if (directory != null && fileName != null) {
                    File file = new File(directory, fileName);
                    String json = StorageManager.read(file.getAbsolutePath());
                    
                    Task newTask = JsonUtil.toTask(json);
                    if (newTask != null) {
                        task.setOperations(newTask.copy());
                        StorageManager.saveToFile();
                        reloadList();
                        flushGlobalUI();
                    }
                }
            }
        });
        
        buttonExport = new RoundedButton("导出");
        buttonExport.setMargin(new Insets(0, 0, 0, 0));
        buttonExport.setBackground(colorBackgroundExport);
        ColorUIManager.setTextSize(buttonExport, textSizeButton);
        ColorUIManager.setTextColor(buttonExport, colorTextLight);
        ColorUIManager.setDefaultIcon(buttonExport, "export.png", 20);
        buttonExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
                // 使用 java.awt.FileDialog 调用原生打开窗口
                FileDialog fileDialog = new FileDialog(owner, "导出", FileDialog.SAVE);
                fileDialog.setDirectory(".");
                String fileName = getName(task) + StorageManager.FILE_EXTENSION;
                fileDialog.setFile(fileName);
                owner.setAlwaysOnTop(false);
                fileDialog.setVisible(true);
                owner.setAlwaysOnTop(true);
                fileDialog.setAlwaysOnTop(true);
                
                String directory = fileDialog.getDirectory();
                fileName = fileDialog.getFile();
                
                if (directory != null && fileName != null) {
                    File file = new File(directory, fileName);
                    if (S.isEmpty(fileName)) {
                        fileName = task.getName() + StorageManager.FILE_EXTENSION;
                    }
                    if (!fileName.endsWith(StorageManager.FILE_EXTENSION)) {
                        fileName = file.getName() + StorageManager.FILE_EXTENSION;
                    }
                    file = new File(file.getParent(), fileName);
                    
                    String json = JsonUtil.toJson(task).toString(2);
                    StorageManager.saveToFile(json, file.getAbsolutePath());
                }
            }
        });
        
        
        buttonDelete = new RoundedButton("删除");
        buttonDelete.setMargin(new Insets(0, 0, 0, 0));
        buttonDelete.setBackground(colorBackgroundDelete);
        ColorUIManager.setTextSize(buttonDelete, textSizeButton);
        ColorUIManager.setDefaultIcon(buttonDelete, "delete.png", 20);
        ColorUIManager.setTextColor(buttonDelete, colorTextLight);
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ConfirmDialog.show(owner, buttonDelete, "确定要删除当前脚本吗，该操作无法恢复")) {
                    ActionManager.removeTask(task);
                    StorageManager.saveToFile();
                    dispose();
                }
            }
        });
        int wDelete = 90;
        int hDelete = 40;
        int xDelete = wTopPanel - wDelete - margin;
        int yDelete = margin;
        buttonDelete.setBounds(xDelete, yDelete, wDelete, hDelete);
        topPanel.add(buttonDelete);
        
        int wExport = 90;
        int hExport = 40;
        int xExport = xDelete - wExport - margin;
        int yExport = margin;
        buttonExport.setBounds(xExport, yExport, wExport, hExport);
        topPanel.add(buttonExport);
        
        int wImport = 90;
        int hImport = 40;
        int xImport = xExport - wImport - margin;
        int yImport = margin;
        buttonimport.setBounds(xImport, yImport, wImport, hImport);
        topPanel.add(buttonimport);
        
        rootPanel.add(topPanel);
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
    
    private void initScroll(JPanel rootPanel) {
        
        operationListView = new JPanel();
        operationListView.setLayout(new VerticalFlowLayout(0, 0, 0, true, false));
        operationListView.setBackground(colorBackground);
        
        JScrollPane jScrollPane = new JScrollPane(operationListView);
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
        
        buttonAddOpration = new RoundedButton("插入任务");
        buttonAddOpration.setBackground(colorAddTask);
        buttonAddOpration.setMargin(new Insets(0, 0, 0, 0));
        buttonAddOpration.setBorder(null);
        buttonAddOpration.setFocusable(false);
        ColorUIManager.setDefaultIcon(buttonAddOpration, "img/add.png");
        buttonAddOpration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                task.addOperation(S.intValue(addPositionView.getText()), new Operation(S.now()).setCount(1));
                addPositionView.setText(String.valueOf(task.getOperationSize()));
                StorageManager.saveToFile();
                reloadList();
                flushGlobalUI();
            }
        });
        int xAddOp = margin;
        int yAddOp = margin;
        buttonAddOpration.setBounds(xAddOp, yAddOp, wAddButton, hAddButton);
        container.add(buttonAddOpration);
        
        
        buttonAddSleep = new RoundedButton("插入休眠");
        buttonAddSleep.setBackground(colorAddSleepTask);
        ColorUIManager.setDefaultIcon(buttonAddSleep, "img/add.png");
        buttonAddSleep.setMargin(new Insets(0, 0, 0, 0));
        buttonAddSleep.setBorder(null);
        buttonAddSleep.setFocusable(false);
        buttonAddSleep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                task.addOperation(S.intValue(addPositionView.getText()), new Operation(S.now()).setSleepTime(DEFAULT_SLEEP_TIME).setCount(1));
                addPositionView.setText(String.valueOf(task.getOperationSize()));
                StorageManager.saveToFile();
                reloadList();
                flushGlobalUI();
            }
        });
        int xAddSleepOp = xAddOp + wAddButton + margin;
        int yAddSleepOp = margin;
        buttonAddSleep.setBounds(xAddSleepOp, yAddSleepOp, wAddButton, hAddButton);
        container.add(buttonAddSleep);
        
        int wPositionTitle = 60;
        int hPositionTitle = 30;
        int xPositionTitle = xAddSleepOp + wAddButton + margin * 3;
        int yPositionTitle = margin;
        addPositionTitleView = new JLabel("插入位置:");
        ColorUIManager.setTextColor(addPositionTitleView, colorText);
        addPositionTitleView.setBounds(xPositionTitle, yPositionTitle, wPositionTitle, hPositionTitle);
        container.add(addPositionTitleView);
        
        int wPosition = 50;
        int hPosition = 30;
        int xPosition = xPositionTitle + wPositionTitle + 5;
        int yPosition = margin;
        addPositionView = new JTextField(task.getOperationSize());
        ((PlainDocument) addPositionView.getDocument()).setDocumentFilter(new NumberInputFilter(Integer.MAX_VALUE));
        addPositionView.setBounds(xPosition, yPosition, wPosition, hPosition);
        addPositionView.setHorizontalAlignment(JTextField.CENTER);
        ColorUIManager.setTextSize(addPositionView, textSizeButton);
        ColorUIManager.setTextColor(addPositionView, colorText);
        ColorUIManager.setField(addPositionView, s -> {
        });
        container.add(addPositionView);
        
        int xCountTitle = xPosition + wPosition + margin;
        int yCountTitle = margin;
        int wCountTitle = 60;
        int hCountTitle = 30;
        JLabel countTitleLabel = new JLabel("执行次数:");
        ColorUIManager.setTextColor(countTitleLabel, colorText);
        countTitleLabel.setBounds(xCountTitle, yCountTitle, wCountTitle, hCountTitle);
        container.add(countTitleLabel);
        //
        int xCount = xCountTitle + wCountTitle + margin;
        int yCount = margin;
        int wCount = 60;
        int hCount = 30;
        taskCountView = new JTextField(String.valueOf(task.getCount()));
        taskCountView.setSize(wCount, hCount);
        taskCountView.setLocation(xCount, yCount);
        taskCountView.setHorizontalAlignment(JTextField.CENTER);
        taskCountView.setBackground(ColorUIManager.colorCount);
        ColorUIManager.setField(taskCountView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                task.setCount(S.intValue(taskCountView.getText()));
                StorageManager.saveToFile();
            }
        });
        ColorUIManager.setTextSize(taskCountView, textSizeButton);
        ColorUIManager.setTextColor(taskCountView, colorText);
        ((PlainDocument) taskCountView.getDocument()).setDocumentFilter(new NumberInputFilter(999));
        container.add(taskCountView);
        
        int wTest = 140;
        int hTest = 40;
        int xTest = wOptions - wTest - 30;
        int yTest = hOptions - hTest - 30;
        testButton = new RoundedButton("测试脚本");
        ColorUIManager.setTextColor(testButton, colorTextLight);
        ColorUIManager.setTextSize(testButton, 18);
        ColorUIManager.setDefaultIcon(testButton, "testplay.png", 20);
        testButton.setBackground(colorBackgroundPlay);
        testButton.setBounds(xTest, yTest, wTest, hTest);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        container.add(testButton);
    }
    
    private JPanel getChildItem(final Operation operation) {
        if (operation == null) {
            return null;
        }
        final ItemPanel item = new ItemPanel();
        
        item.setPreferredSize(new Dimension(wItem, hItem));
        item.setLayout(null);
        item.setBackground(colorBackgroundItem);
        
        //
        JTextField taskNameView = new JTextField();
        taskNameView.setSize(wTaskName, hButton);
        ColorUIManager.setTextSize(taskNameView, textSizeTaskName);
        ColorUIManager.setTextColor(taskNameView, colorText);
        taskNameView.setLocation(xTaskName, yTaskName);
        taskNameView.setBackground(colorTaskNameBackground);
        ColorUIManager.setField(taskNameView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                operation.setName(s);
                StorageManager.saveToFile();
            }
        });
        
        //
        JLabel countTitleView = new JLabel("执行次数:");
        countTitleView.setSize(wCountTitle, hButton);
        countTitleView.setLocation(xCountTitle, yButton);
        countTitleView.setOpaque(true);
        countTitleView.setHorizontalAlignment(SwingConstants.LEFT);
        countTitleView.setVerticalAlignment(SwingConstants.CENTER);
        countTitleView.setBackground(colorTaskNameBackground);
        ColorUIManager.setTextSize(countTitleView, textSizeButton);
        ColorUIManager.setTextColor(countTitleView, colorText);
        //
        final JTextField countView = new JTextField(String.valueOf(operation.getCount()));
        countView.setSize(wCount, hButton);
        countView.setLocation(xCount, yButton + 2);
        countView.setHorizontalAlignment(JTextField.CENTER);
        countView.setBackground(ColorUIManager.colorCount);
        ColorUIManager.setField(countView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                operation.setCount(S.intValue(countView.getText()));
                StorageManager.saveToFile();
            }
        });
        ColorUIManager.setTextSize(countView, textSizeButton);
        ColorUIManager.setTextColor(countView, colorText);
        ((PlainDocument) countView.getDocument()).setDocumentFilter(new NumberInputFilter(999));
        //
        final JButton buttonRecord = new RoundedButton();
        ColorUIManager.setDefaultIcon(buttonRecord, "record.png");
        ColorUIManager.setTextColor(buttonRecord, colorTextLight);
        buttonRecord.setMargin(new Insets(0, 0, 0, 0));
        buttonRecord.setText("录制");
        buttonRecord.setBackground(colorBackgroundRecord);
        buttonRecord.setBorder(null);
        buttonRecord.setFocusable(false);
        buttonRecord.setSize(wButton, hButton);
        buttonRecord.setLocation(xRecord, yButton);
        buttonRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RecordManager.startWaitingRecording(operation, new RecordManager.Callback() {
                    @Override
                    public void onEnd(Operation operation) {
                        setTitle("[" + operation.getName() + "]录制完毕,共包含" + operation.size() + "帧");
                        ActionManager.setWidthGameWindow(WindowPositionManager.wGameWindow);
                        ActionManager.setHeightGameWindow(WindowPositionManager.hGameWindow);
                        StorageManager.saveToFile();
                        flushGlobalUI();
                    }
                    
                    @Override
                    public void onCancel(Operation operation) {
                        setTitle("[" + operation.getName() + "]录制已取消");
                        flushGlobalUI();
                    }
                    
                    @Override
                    public void onWaiting(Operation operation) {
                        setTitle("等待录制，按下F12开始，Esc或Pause取消");
                        flushGlobalUI();
                    }
                    
                    @Override
                    public void onStart(Operation operation) {
                        setTitle("[" + operation.getName() + "]正在录制");
                        flushGlobalUI();
                    }
                });
            }
        });
        //
        final RoundedButton buttonPlay = new RoundedButton();
        buttonPlay.setText("测试");
        ColorUIManager.setDefaultIcon(buttonPlay, "testplay.png");
        ColorUIManager.setTextColor(buttonPlay, colorTextLight);
        buttonPlay.setBackground(colorBackgroundPlay);
        buttonPlay.setSize(wButton, hButton);
        buttonPlay.setBorder(null);
        buttonPlay.setFocusable(false);
        buttonPlay.setMargin(new Insets(0, 0, 0, 0));
        buttonPlay.setLocation(xPlay, yButton);
        buttonPlay.setBorderPainted(false);
        buttonPlay.setFocusPainted(false);
        
        buttonPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PlayManager.startPlay(task, operation, new TaskExecutor.Callback() {
                    @Override
                    public void onRun(Operation operation, int countOperation, int count) {
                        setTitle(getName(operation) + " " + countOperation + "/" + operation.getCount());
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
        
        JButton buttonDelete = getDeleteButton(operation, item);
        buttonDelete.setLocation(xDelete, yButton);
        item.setFlushUI(new ItemPanel.UIFlusher() {
            @Override
            public void onFlushUI() {
                taskNameView.setText(getName(operation));
                taskNameView.setCaretPosition(0);
                
                Operation playingOperation = PlayManager.isPlaying() ? PlayManager.getPlayingOperation() : null;
                boolean isPlaying = PlayManager.isPlaying();
                boolean isRecording = RecordManager.isRecording();
                boolean enable = !isPlaying && !isRecording;
                boolean isCurrentPlaying = playingOperation != null && playingOperation.getCreateTime() == operation.getCreateTime();
                boolean gameExists = WindowPositionManager.gameWindowExists;
                
                buttonPlay.setLoading(isCurrentPlaying);
                
                buttonPlay.setEnabled(gameExists && enable && !operation.isEmpty());
                buttonRecord.setEnabled(gameExists && enable);
                buttonDelete.setEnabled(enable);
                
                taskNameView.setEnabled(enable);
                countView.setEnabled(enable);
                
                item.setBackground(isCurrentPlaying ? colorBackgroundItemPlaying : colorBackgroundItem);
            }
        });
        
        
        addItemMouseColor(operation, item, buttonRecord, buttonDelete, item, taskNameView, countView, buttonPlay);
        
        item.add(taskNameView);
        item.add(countView);
        item.add(buttonRecord);
        item.add(buttonPlay);
        item.add(countTitleView);
        item.add(buttonDelete);
        
        item.flushUI();
        
        return item;
    }
    
    private JPanel getSleepItem(final Operation operation) {
        if (task == null || operation == null) {
            return null;
        }
        int hItemSleep = (int) (hItem * 1f);
        final ItemPanel item = new ItemPanel();
        item.setPreferredSize(new Dimension(wItem, hItemSleep));
        item.setBorder(null);
        item.setLayout(null);
        item.setBackground(colorBackgroundItemSleep);
        
        int wSleepTitle = wCountTitle;
        int xSleepTitle = xTaskName;
        
        int xSleepTimeTitle = xCountTitle;
        int ySleepTimeTitle = (hItemSleep - hButton) / 2;
        
        int wSleepTime = wCount;
        int xSleepTime = xCount;
        int ySleepTime = ySleepTimeTitle;
        
        int yDelete = ySleepTimeTitle;
        //
        final JTextField taskNameView = new JTextField(getName(operation));
        taskNameView.setSize(wTaskName, hButton);
        taskNameView.setLocation(xSleepTitle, ySleepTime);
        ColorUIManager.setTextSize(taskNameView, textSizeTaskName);
        ColorUIManager.setTextColor(taskNameView, colorTextRed);
        ColorUIManager.setField(taskNameView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                operation.setName(s);
                StorageManager.saveToFile();
            }
        });
        taskNameView.setBackground(colorBackgroundLight);
        taskNameView.setHorizontalAlignment(JTextField.LEFT);
        
        //
        JLabel jLabelSleepTimeTitle = new JLabel("时长(毫秒)");
        jLabelSleepTimeTitle.setSize(wSleepTitle, hButton);
        jLabelSleepTimeTitle.setLocation(xSleepTimeTitle, ySleepTimeTitle);
        jLabelSleepTimeTitle.setHorizontalAlignment(SwingConstants.LEFT);
        jLabelSleepTimeTitle.setVerticalAlignment(SwingConstants.CENTER);
        ColorUIManager.setTextSize(jLabelSleepTimeTitle, textSizeButton);
        ColorUIManager.setTextColor(jLabelSleepTimeTitle, colorText);
        
        final JTextField sleepTimeView = new JTextField(String.valueOf(operation.getSleepTime()));
        sleepTimeView.setSize(wSleepTime, hButton);
        sleepTimeView.setLocation(xSleepTime, ySleepTime);
        ColorUIManager.setTextSize(sleepTimeView, textSizeButton);
        ColorUIManager.setTextColor(sleepTimeView, colorText);
        ColorUIManager.setField(sleepTimeView, new Callback<String>() {
            @Override
            public void onResult(String s) {
                operation.setSleepTime(S.max(0, S.longValue(sleepTimeView.getText())));
                StorageManager.saveToFile();
            }
        });
        sleepTimeView.setBackground(colorBackgroundLight);
        sleepTimeView.setHorizontalAlignment(JTextField.CENTER);
        ((PlainDocument) sleepTimeView.getDocument()).setDocumentFilter(new NumberInputFilter(Integer.MAX_VALUE));
        
        JButton buttonDelete = getDeleteButton(operation, item);
        buttonDelete.setLocation(xDelete, yDelete);
        
        item.setFlushUI(new ItemPanel.UIFlusher() {
            @Override
            public void onFlushUI() {
                boolean enable = !PlayManager.isPlaying() && !RecordManager.isRecording();
                taskNameView.setEnabled(enable);
                sleepTimeView.setEnabled(enable);
                buttonDelete.setEnabled(enable);
                
                Operation playingOperation = PlayManager.isPlaying() ? PlayManager.getPlayingOperation() : null;
                boolean isCurrentPlaying = PlayManager.isPlaying() && playingOperation != null && playingOperation.getCreateTime() == operation.getCreateTime();
                item.setBackground(isCurrentPlaying ? colorBackgroundItemPlaying : colorBackgroundItemSleep);
            }
        });
        addItemMouseColor(operation, item, item, sleepTimeView, buttonDelete);
        
        item.add(taskNameView);
        item.add(jLabelSleepTimeTitle);
        item.add(sleepTimeView);
        item.add(buttonDelete);
        
        item.flushUI();
        
        return item;
    }
    
    private RoundedButton getDeleteButton(Operation operation, ItemPanel item) {
        RoundedButton buttonDelete = new RoundedButton();
        buttonDelete.setMargin(new Insets(0, 0, 0, 0));
        buttonDelete.setText("删除");
        buttonDelete.setBackground(colorBackgroundDelete);
        buttonDelete.setBorder(null);
        buttonDelete.setFocusable(false);
        buttonDelete.setSize(wButton, hButton);
        ColorUIManager.setDefaultIcon(buttonDelete, "delete.png");
        ColorUIManager.setTextSize(buttonDelete, textSizeButton);
        ColorUIManager.setTextColor(buttonDelete, colorTextLight);
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ConfirmDialog.show(owner, buttonDelete, "确定要删除该任务？")) {
                    task.removeOperation(operation);
                    setTitle("已删除任务");
                    StorageManager.saveToFile();
                    if (!task.contains(operation)) {
                        operationListView.remove(item);
                        operationListView.revalidate();
                        operationListView.repaint();
                    }
                    flushGlobalUI();
                }
            }
        });
        return buttonDelete;
    }
    
    private void addItemMouseColor(Operation operation, JPanel item, JComponent... components) {
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
                    item.setBackground(operation.getSleepTime() > 0 ? colorBackgroundItemSleep : colorBackgroundItem);
                }
            }
        };
        for (JComponent component : components) {
            component.addMouseListener(mouseListener);
        }
    }
    
    private void flushGlobalUI() {
        boolean enable = !PlayManager.isPlaying() && !RecordManager.isRecording();
        buttonAddSleep.setEnabled(enable);
        buttonAddOpration.setEnabled(enable);
        buttonExport.setEnabled(enable);
        buttonimport.setEnabled(enable);
        buttonDelete.setEnabled(enable);
        addPositionView.setText(String.valueOf(task.getOperationSize()));
        taskCountView.setText(String.valueOf(task.getCount()));
        testButton.setEnabled(WindowPositionManager.gameWindowExists && enable);
        
        
        Task playingTask = PlayManager.isPlaying() ? PlayManager.getPlayingTask() : null;
        boolean isCurrentPlaying = PlayManager.isPlaying() && playingTask != null && playingTask.getCreateTime() == task.getCreateTime();
        boolean isPlayingWholeTask = PlayManager.isPlayingWholeTask();
        testButton.setLoading(isCurrentPlaying && isPlayingWholeTask);
        flushList();
    }
    
    private void flushList() {
        int childCount = operationListView.getComponentCount();
        for (int i = 0; i < childCount; i++) {
            Component component = operationListView.getComponent(i);
            if (component instanceof ItemPanel itemPanel) {
                itemPanel.flushUI();
            }
        }
    }
    
    private void reloadList() {
        operationListView.removeAll();
        List<Operation> operations = task.copy();
        for (int i = 0; i < operations.size(); i++) {
            Operation operation = operations.get(i);
            JPanel jPanel = operation.getSleepTime() > 0 ? getSleepItem(operation) : getChildItem(operation);
            operationListView.add(jPanel);
        }
        operationListView.revalidate();
        operationListView.repaint();
    }
    
    @Override
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getTaskName() {
        return taskNameView.getText();
    }
    
    public int getTaskCount() {
        try {
            return Integer.parseInt(taskNameView.getText());
        } catch (NumberFormatException e) {
            return task.getCount();
        }
    }
    
    public String getName(Task task) {
        return S.isEmpty(task.getName()) ? "脚本" + task.getCreateTime() : task.getName();
    }
    
    public String getName(Operation operation) {
        return !S.isEmpty(operation.getName()) ? operation.getName() : (operation.getSleepTime() > 0 ? "休眠任务" : "任务" + operation.getCreateTime());
    }
}