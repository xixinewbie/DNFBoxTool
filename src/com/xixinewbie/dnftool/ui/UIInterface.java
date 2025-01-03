package com.xixinewbie.dnftool.ui;

import com.xixinewbie.dnftool.manager.*;
import com.xixinewbie.dnftool.model.Global;
import com.xixinewbie.dnftool.model.Task;
import com.xixinewbie.dnftool.ui.components.NumberInputFilter;
import com.xixinewbie.dnftool.ui.components.VerticalFlowLayout;
import com.xixinewbie.dnftool.util.FileUtil;
import com.xixinewbie.dnftool.util.JsonUtil;
import com.xixinewbie.dnftool.util.S;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class UIInterface extends JFrame {

    private final Color colorRecord = Color.decode("#8cdd8c");
    private final Color colorPlay = Color.decode("#dddd8c");
    private final Color colorDelete = Color.decode("#dd8c8c");
    private final Color colorAddTask = Color.decode("#8cdd8c");
    private final Color colorAddSleepTask = Color.decode("#d19275");
    private final Color colorPlayGlobal = Color.decode("#8c8cdd");
    private final Color colorSave = Color.decode("#ffaa66");
    private final Color colorLoad = Color.decode("#ffaa66");
    private final Color colorTaskListViewBackground = Color.decode("#fbfbfb");
    private final Color colorItemBackground = Color.decode("#ffffff");
    private final Color colorItemBackgroundSleep = Color.decode("#efefef");
    private final Color colorItemBackgroundPlaying = Color.decode("#ff9977");
    private final Color colorItemBackgroundSelected = Color.decode("#e0e0ff");
    private final Color colorButtonDisabled = Color.decode("#efefef");

    private static String srt1 = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static String srt2 = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    private static String srt3 = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static String srt4 = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
    private static String srt5 = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static String srt6 = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    private static final long MIN_SLEEP_TIME = 200;

    private int margin = 12;
    private int wRootPanel = 400;
    private int hRootPanel = 400;
    private int xRootPanel = 0;
    private int yRootPanel = 0;

    private int wScroll = wRootPanel - margin * 2;
    private int hScroll = 200;
    private int xScroll = margin;
    private int yScroll = margin;

    private int wOperation = wRootPanel;
    private int hOperation = hRootPanel - hScroll - margin;
    private int xOperation = 0;
    private int yOperation = yScroll + hScroll + margin;

    private int xWindow, yWindow;
    private int xMouse, yMouse;

    private JPanel taskListView;
    private JButton buttonAddTask;
    private JButton buttonAddSleepTask;
    private JLabel addPositionTitleView;
    private JTextField addPositionView;
    private JTextField countText;
    private JButton buttonSave;
    private JButton buttonLoad;
    private JButton buttonPlayGlobal;
    private JCheckBox jumpMoveCheckBox;
    private JCheckBox highSpeedCheckBox;

    private DocumentListener globalCountChangeListener;
    private ItemListener jumpMouseMoveListener;
    private ItemListener highSpeedListener;

    public UIInterface() {

        setFonts(this);
        //设置主题
//        try {
//            UIManager.setLookAndFeel(srt1);
//            SwingUtilities.updateComponentTreeUI(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JFrame.setDefaultLookAndFeelDecorated(true);

        setResizable(false);
        UIManager.put("Button.disabledBackground", colorButtonDisabled);
        ImageIcon icon = new ImageIcon("icon.png");
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

        JPanel bigPanel = initRootPanel();

        initScroll(bigPanel);

        JPanel downPanel = initOperationPanel(bigPanel);
        initButtonPanel(downPanel);

        setAlwaysOnTop(true);
        pack();
        setLocation(w_screen - getWidth() - 25, h_screen - getHeight() - 50);
        this.setVisible(true);

        initUIData();
    }

    public static void setFonts(JFrame frame) {
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
    }

    public JPanel initRootPanel() {
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(null);
        bigPanel.setPreferredSize(new Dimension(wRootPanel, hRootPanel));
        bigPanel.setLocation(xRootPanel, yRootPanel);
        bigPanel.setBackground(Color.WHITE);
        add(bigPanel);
        return bigPanel;
    }

    private void initScroll(JPanel bigPanel) {
        taskListView = new JPanel();
        taskListView.setLayout(new VerticalFlowLayout(0, 0, 0, true, false));
        taskListView.setBackground(colorTaskListViewBackground);

        JScrollPane jScrollPane = new JScrollPane(taskListView);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        jScrollPane.setBounds(xScroll, yScroll, wScroll, hScroll);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        bigPanel.add(jScrollPane);
    }

    public JPanel initOperationPanel(JPanel container) {
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(null);
        operationPanel.setBounds(xOperation, yOperation, wOperation, hOperation);
        operationPanel.setBackground(Color.white);
        container.add(operationPanel);
        return operationPanel;
    }

    public void initButtonPanel(JPanel container) {
        int w_container = container.getWidth();
        int h_container = container.getHeight();
        int w_view = w_container;

        int countChild = 4;

        int wAddButton = 80;
        int hAddButton = 30;

        buttonAddTask = new JButton("插入子任务");
        buttonAddTask.setBackground(colorAddTask);
        buttonAddTask.setMargin(new Insets(0, 0, 0, 0));
        buttonAddTask.setBorder(null);
        buttonAddTask.setFocusable(false);
        buttonAddTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ActionManager.createTask(S.intValue(addPositionView.getText()), new Task(S.now()).setCount(1).setAccess(true));
                saveToFile();
                flushGlobalUI(true);
            }
        });
        int xAddTask = margin;
        int yAddTask = 0;
        buttonAddTask.setBounds(xAddTask, yAddTask, wAddButton, hAddButton);
        container.add(buttonAddTask);

        buttonAddSleepTask = new JButton("插入休眠");
        buttonAddSleepTask.setBackground(colorAddSleepTask);
        buttonAddSleepTask.setMargin(new Insets(0, 0, 0, 0));
        buttonAddSleepTask.setBorder(null);
        buttonAddSleepTask.setFocusable(false);
        buttonAddSleepTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ActionManager.createTask(S.intValue(addPositionView.getText()), new Task(S.now()).setSleepTime(MIN_SLEEP_TIME).setCount(1).setAccess(true));
                saveToFile();
                flushGlobalUI(true);
            }
        });
        int xAddSleepTask = xAddTask + wAddButton + margin;
        int yAddSleepTask = 0;
        buttonAddSleepTask.setBounds(xAddSleepTask, yAddSleepTask, wAddButton, hAddButton);
        container.add(buttonAddSleepTask);

        int wPositionTitle = 80;
        int hPositionTitle = 30;
        int xPositionTitle = xAddSleepTask + wAddButton + margin;
        int yPositionTitle = 0;
        addPositionTitleView = new JLabel("插入位置");
        addPositionTitleView.setBounds(xPositionTitle, yPositionTitle, wPositionTitle, hPositionTitle);
        container.add(addPositionTitleView);

        int wPosition = 50;
        int hPosition = 30;
        int xPosition = xPositionTitle + wPosition + 5;
        int yPosition = 0;
        addPositionView = new JTextField();
        ((PlainDocument) addPositionView.getDocument()).setDocumentFilter(new NumberInputFilter());
        addPositionView.setBounds(xPosition, yPosition, wPosition, hPosition);
        addPositionView.setHorizontalAlignment(JTextField.CENTER);
        container.add(addPositionView);

        int wButton = 120;
        int hButton = 40;

        buttonPlayGlobal = new JButton();
        buttonPlayGlobal.setContentAreaFilled(true);
        buttonPlayGlobal.setFont(UIManager.getFont("large.font"));
        buttonPlayGlobal.setMargin(new Insets(0, 0, 0, 0));
        buttonPlayGlobal.setBackground(colorPlayGlobal);
        buttonPlayGlobal.setBorder(null);
        buttonPlayGlobal.setFocusable(false);
        buttonPlayGlobal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                play(null);
            }
        });
        int wPlay = 180;
        int hPlay = 60;
        int xPlay = container.getWidth() - wPlay - margin;
        int yPlay = container.getHeight() - hPlay - margin * 2;
        buttonPlayGlobal.setBounds(xPlay, yPlay, wPlay, hPlay);
        container.add(buttonPlayGlobal);

        buttonSave = new JButton("保存");
        buttonSave.setContentAreaFilled(true);
        buttonSave.setMargin(new Insets(0, 0, 0, 0));
        buttonSave.setBackground(colorSave);
        buttonSave.setBorder(null);
        buttonSave.setFocusable(false);
        buttonSave.setFocusable(false);
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setFileFilter(new FileNameExtensionFilter("配置文件", "init"));
                chooser.setMultiSelectionEnabled(false);
                int result = chooser.showSaveDialog(buttonSave);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (file == null) {
                        return;
                    }
                    String fileName = file.getName();
                    if (S.isEmpty(fileName)) {
                        fileName = "未命名.init";
                    }
                    if (!fileName.endsWith(".init")) {
                        fileName = file.getName() + ".init";
                    }
                    file = new File(file.getParent(), fileName);
                    S.s("save to :" + file.getAbsoluteFile());
                    saveConfigToFile(file);
                }
            }
        });
        int xSave = margin;
        int ySave = container.getHeight() - hButton - margin * 2;
        int wSave = 60;
        int hSave = 40;
        buttonSave.setBounds(xSave, ySave, wSave, hSave);
        container.add(buttonSave);

        buttonLoad = new JButton("加载");
        buttonLoad.setContentAreaFilled(true);
        buttonLoad.setBorder(null);
        buttonLoad.setFocusable(false);
        buttonLoad.setMargin(new Insets(0, 0, 0, 0));
        buttonLoad.setBackground(colorLoad);
        buttonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new FileNameExtensionFilter("配置文件(.init)", "init"));
                int result = chooser.showOpenDialog(buttonSave);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    S.s("load from :" + file.getAbsoluteFile());
                    readFromFile(file.getAbsolutePath());
                    saveToFile();
                    flushGlobalUI(true);
                }
            }
        });
        int xLoad = xSave + wSave + margin;
        int yLoad = container.getHeight() - hButton - margin * 2;
        int wLoad = 60;
        int hLoad = 40;
        buttonLoad.setBounds(xLoad, yLoad, wLoad, hLoad);
        container.add(buttonLoad);

        int xCountLabel = margin * 2;
        int yCountLabel = yAddTask + margin + hAddButton;
        int wCountLabel = 50;
        int hCountLabel = hAddButton;
        JLabel countLabel = new JLabel("执行次数");
        countLabel.setBounds(xCountLabel, yCountLabel, wCountLabel, hCountLabel);
        container.add(countLabel);

        int xCount = xCountLabel + wCountLabel + margin;
        int yCount = yCountLabel;
        int wCount = 60;
        int hCount = hAddButton;
        countText = new JTextField();
        countText.setEditable(true);
        countText.setBounds(xCount, yCount, wCount, hCount);
        container.add(countText);
        globalCountChangeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ActionManager.global.count = S.intValue(countText.getText());
                saveToFile();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ActionManager.global.count = S.intValue(countText.getText());
                saveToFile();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ActionManager.global.count = S.intValue(countText.getText());
                saveToFile();
            }
        };
        jumpMouseMoveListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ActionManager.global.ignoreMove = jumpMoveCheckBox.isSelected();
                saveToFile();
            }
        };
        highSpeedListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ActionManager.global.highSpeed = highSpeedCheckBox.isSelected();
                saveToFile();
            }
        };
        ((PlainDocument) countText.getDocument()).setDocumentFilter(new NumberInputFilter());

        countText.setHorizontalAlignment(JTextField.CENTER);

        int xJumpMoveLabel = xCount + wCount + margin;
        int yJumpMoveLabel = yCount;
        int wJumpMoveLabel = 80;
        int hJumpMoveLabel = hAddButton;
        JLabel JumpMoveLabel = new JLabel("跳过鼠标移动");
        JumpMoveLabel.setBounds(xJumpMoveLabel, yJumpMoveLabel, wJumpMoveLabel, hJumpMoveLabel);
        container.add(JumpMoveLabel);

        int xJumpMove = xJumpMoveLabel + wJumpMoveLabel;
        int yJumpMove = yJumpMoveLabel;
        int wJumpMove = 30;
        int hJumpMove = hAddButton;
        jumpMoveCheckBox = new JCheckBox();
        jumpMoveCheckBox.setBackground(null);
        jumpMoveCheckBox.setBounds(xJumpMove, yJumpMove, wJumpMove, hJumpMove);
        container.add(jumpMoveCheckBox);


        int xHighSpeedLabel = xJumpMove + wJumpMove + margin;
        int yHighSpeedLabel = yCount;
        int wHighSpeedLabel = 65;
        int hHighSpeedLabel = hAddButton;
        JLabel HighSpeedLabel = new JLabel("点击低延迟");
        HighSpeedLabel.setBounds(xHighSpeedLabel, yHighSpeedLabel, wHighSpeedLabel, hHighSpeedLabel);
        container.add(HighSpeedLabel);

        int xHighSpeed = xHighSpeedLabel + wHighSpeedLabel;
        int yHighSpeed = yHighSpeedLabel;
        int wHighSpeed = 30;
        int hHighSpeed = hAddButton;
        highSpeedCheckBox = new JCheckBox();
        highSpeedCheckBox.setBackground(null);
        highSpeedCheckBox.setBounds(xHighSpeed, yHighSpeed, wHighSpeed, hHighSpeed);
        container.add(highSpeedCheckBox);

    }

    private void saveConfigToFile(File file) {
        FileUtil.saveConfigToFile(JsonUtil.toJson(ActionManager.global), file);
    }

    private JPanel getSleepItem(final Task task, int index) {
        if (task == null) {
            return null;
        }
        final JPanel item = new JPanel();
        int wItem = wScroll - 15;
        int hItem = 40;
        item.setPreferredSize(new Dimension(wItem, hItem));
        item.setBorder(null);
        item.setLayout(null);

        int wAccess = 30;
        int wTaskName = 125;
        int xAccess = 10;
        int wButton = 50;
        int hButton = 30;
        int wSleepTimeTitle = 80;
        int wSleepTime = 80;
        int xSleepTimeTitle = (wItem - wSleepTimeTitle - wSleepTime - margin) / 2;
        int xSleepTime = xSleepTimeTitle + wSleepTimeTitle + margin;
        int xDelete = wItem - wButton - 5;
        //
        JCheckBox accessCheckBox = new JCheckBox();
        accessCheckBox.setSelected(task.isAccess());
        accessCheckBox.setSize(new Dimension(wAccess, wAccess));
        accessCheckBox.setLocation(xAccess, 5);
        accessCheckBox.setBackground(null);
        //
        JLabel jLabelSleepTimeTitle = new JLabel("休眠时长(毫秒)");
        jLabelSleepTimeTitle.setSize(wSleepTimeTitle, hButton);
        jLabelSleepTimeTitle.setLocation(xSleepTimeTitle, 5);

        final JTextField sleepTimeView = new JTextField(String.valueOf(task.getSleepTime()));
        sleepTimeView.setSize(wSleepTime, hButton);
        sleepTimeView.setLocation(xSleepTime, 5);
        sleepTimeView.setHorizontalAlignment(JTextField.CENTER);
        ((PlainDocument) sleepTimeView.getDocument()).setDocumentFilter(new NumberInputFilter());
        sleepTimeView.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                task.setSleepTime(S.max(MIN_SLEEP_TIME, S.longValue(sleepTimeView.getText())));
                saveToFile();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                task.setSleepTime(S.max(MIN_SLEEP_TIME, S.longValue(sleepTimeView.getText())));
                saveToFile();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                task.setSleepTime(S.max(MIN_SLEEP_TIME, S.longValue(sleepTimeView.getText())));
                saveToFile();
            }
        });

        final JButton buttonDelete = new JButton();
        buttonDelete.setMargin(new Insets(0, 0, 0, 0));
        buttonDelete.setText("删除");
        buttonDelete.setBackground(colorDelete);
        buttonDelete.setBorder(null);
        buttonDelete.setFocusable(false);
        buttonDelete.setSize(wButton, hButton);
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object[] options = {" 确定 ", " 取消 "};
                int response = JOptionPane.showOptionDialog(buttonDelete, "确定要删除该等待任务吗？", "删除", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0) {
                    ActionManager.removeTask(task);
                    saveToFile();
                    flushGlobalUI(true);
                    setTitle("已删除等待任务");
                }
            }
        });
        buttonDelete.setLocation(xDelete, 5);

        final Call call = new Call() {
            @Override
            public void initView() {
                boolean enable = isButtonEnabled();
                boolean access = task.isAccess();
                accessCheckBox.setEnabled(enable);
                sleepTimeView.setEnabled(enable);
                jLabelSleepTimeTitle.setEnabled(access && enable);
                buttonDelete.setEnabled(enable);
                flushGlobalUI(false);

                Task playingTask = PlayManager.isPlaying() ? PlayManager.getPlayingTask() : null;
                boolean isPlaying = PlayManager.isPlaying() && playingTask != null && playingTask.id == task.id;
                item.setBackground(isPlaying ? colorItemBackgroundPlaying : colorItemBackgroundSleep);
            }
        };

        accessCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                task.setAccess(accessCheckBox.isSelected());
                saveToFile();
                call.initView();
            }
        });

        addItemMouseColor(task, item, item, accessCheckBox, sleepTimeView, buttonDelete);

        item.add(accessCheckBox);
        item.add(jLabelSleepTimeTitle);
        item.add(sleepTimeView);
        item.add(buttonDelete);

        call.initView();

        return item;
    }

    private JPanel getItem(final Task task, int index) {
        if (task == null) {
            return null;
        }
        final JPanel item = new JPanel();
        int wItem = wScroll - 15;
        int hItem = 40;
        item.setPreferredSize(new Dimension(wItem, hItem));
        item.setBorder(null);
        item.setLayout(null);

        int wButton = 50;
        int hButton = 30;
        int wAccess = 30;
        int wTaskName = 90;
        int xAccess = 10;
        int xName = xAccess + wAccess;
        int xDelete = wItem - wButton - 5;
        int xRun = xDelete - wButton - 5;
        int xReset = xRun - wButton - 5;
        int xCount = xReset - wButton - 5;
        //
        JCheckBox accessCheckBox = new JCheckBox();
        accessCheckBox.setSelected(task.isAccess());
        accessCheckBox.setSize(new Dimension(wAccess, wAccess));
        accessCheckBox.setBackground(null);
        accessCheckBox.setLocation(xAccess, 5);
        //
        JLabel jLabelName = new JLabel();
        jLabelName.setSize(wTaskName, hButton);
        jLabelName.setLocation(xName, 5);

        //
        final JTextField labelCount = new JTextField(String.valueOf(task.getCount()));
        labelCount.setSize(wButton, hButton);
        labelCount.setLocation(xCount, 5);
        labelCount.setHorizontalAlignment(JTextField.CENTER);
        ((PlainDocument) labelCount.getDocument()).setDocumentFilter(new NumberInputFilter());
        //
        final JButton buttonRecord = new JButton("录制");
        buttonRecord.setSize(50, 30);
        buttonRecord.setBackground(colorRecord);
        buttonRecord.setBorder(null);
        buttonRecord.setFocusable(false);
        buttonRecord.setMargin(new Insets(0, 0, 0, 0));
        buttonRecord.setLocation(xReset, 5);
        //

        final JButton buttonDelete = new JButton();
        buttonDelete.setMargin(new Insets(0, 0, 0, 0));
        buttonDelete.setText("删除");
        buttonDelete.setBackground(colorDelete);
        buttonDelete.setBorder(null);
        buttonDelete.setFocusable(false);
        buttonDelete.setSize(wButton, hButton);
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object[] options = {" 确定 ", " 取消 "};
                int response = JOptionPane.showOptionDialog(buttonDelete, "确定删除该任务吗？", "删除任务", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0) {
                    ActionManager.removeTask(task);
                    saveToFile();
                    flushGlobalUI(true);
                    setTitle("已删除" + getName(task));
                }
            }
        });
        buttonDelete.setLocation(xDelete, 5);
        //
        final JButton buttonPlay = new JButton();
        buttonPlay.setText("执行");
        buttonPlay.setBackground(colorPlay);
        buttonPlay.setSize(wButton, hButton);
        buttonPlay.setBorder(null);
        buttonPlay.setFocusable(false);
        buttonPlay.setMargin(new Insets(0, 0, 0, 0));
        buttonPlay.setLocation(xRun, 5);

        final Call call = new Call() {
            @Override
            public void initView() {
                jLabelName.setText(getName(task));

                boolean enable = isButtonEnabled();
                boolean access = task.isAccess();
                boolean gameExists = isGameExists();

                accessCheckBox.setEnabled(enable);
                buttonPlay.setEnabled(gameExists && access && enable && !task.isEmpty());
                buttonDelete.setEnabled(enable);
                buttonRecord.setEnabled(gameExists && access && enable);
                jLabelName.setEnabled(enable);
                labelCount.setEnabled(enable);
                flushGlobalUI(false);

                Task playingTask = PlayManager.isPlaying() ? PlayManager.getPlayingTask() : null;
                boolean isPlaying = PlayManager.isPlaying() && playingTask != null && playingTask.id == task.id;
                item.setBackground(isPlaying ? colorItemBackgroundPlaying : colorItemBackground);
                buttonPlay.setText(isPlaying ? "执行中" : "执行");
            }
        };
        accessCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                task.setAccess(accessCheckBox.isSelected());
                saveToFile();
                call.initView();
            }
        });
        labelCount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                task.setCount(S.intValue(labelCount.getText()));
                saveToFile();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                task.setCount(S.intValue(labelCount.getText()));
                saveToFile();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                task.setCount(S.intValue(labelCount.getText()));
                saveToFile();
            }
        });
        buttonPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                play(task);
                call.initView();
            }
        });
        jLabelName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isButtonEnabled()) {
                    return;
                }
                String result = JOptionPane.showInputDialog(jLabelName, "请输入任务名称", task.getName());
                if (!S.isEmpty(result)) {
                    task.setName(result);
                    saveToFile();
                    call.initView();
                }
            }
        });
        buttonRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object[] options = {"确定", "取消"};
                RecordManager.startWaitingRecording(task, new RecordManager.Callback() {
                    @Override
                    public void onEnd(Task task) {
                        setTitle("[" + task.getName() + "]录制完毕,共包含" + task.size() + "帧");
                        ActionManager.global.setWidthWindow(WindowPositionManager.wGameWindow);
                        ActionManager.global.setHeightWindow(WindowPositionManager.hGameWindow);
                        saveToFile();
                        flushGlobalUI(true);
                    }

                    @Override
                    public void onCancel(Task task) {
                        setTitle("[" + task.getName() + "]录制已取消");
                        flushGlobalUI(true);
                    }

                    @Override
                    public void onWaiting(Task task) {
                        setTitle("等待录制，按下F12开始，Esc或Pause取消");
                        flushGlobalUI(true);
                    }

                    @Override
                    public void onStart(Task task) {
                        setTitle("[" + task.getName() + "]正在录制");
                        flushGlobalUI(true);
                    }
                });
            }
        });

        addItemMouseColor(task, item, accessCheckBox, buttonRecord, item, jLabelName, labelCount, buttonPlay);

        item.add(accessCheckBox);
        item.add(jLabelName);
        item.add(labelCount);
        item.add(buttonRecord);
        item.add(buttonDelete);
        item.add(buttonPlay);

        call.initView();

        return item;
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
                if (!isRecording() && !isPlaying()) {
                    item.setBackground(colorItemBackgroundSelected);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (!isRecording() && !isPlaying()) {
                    item.setBackground(task.getSleepTime() > 0 ? colorItemBackgroundSleep : colorItemBackground);
                }
            }
        };
        for (JComponent component : components) {
            component.addMouseListener(mouseListener);
        }
    }

    private void initUIData() {
        readFromFile(FileUtil.FILE_PATH);
        WindowPositionManager.detector = new WindowPositionManager.WindowDetector() {
            @Override
            public void onWindowChange(boolean exists) {
                if (!exists) {
                    setTitle("未检测到游戏窗口");
                } else {
                    setTitle("DNF脚本录制器  by西西同学");
                }
                flushGlobalUI(true);
            }
        };
        WindowPositionManager.init();
        Global global = ActionManager.global;

        Document document = countText.getDocument();
        countText.setText(String.valueOf(global == null ? 1 : global.count));
        document.addDocumentListener(globalCountChangeListener);

        flushGlobalUI(true);
    }

    private void flushGlobalUI(boolean includeList) {

        jumpMoveCheckBox.removeItemListener(jumpMouseMoveListener);
        jumpMoveCheckBox.setSelected(ActionManager.global.ignoreMove);
        jumpMoveCheckBox.addItemListener(jumpMouseMoveListener);

        highSpeedCheckBox.removeItemListener(highSpeedListener);
        highSpeedCheckBox.setSelected(ActionManager.global.highSpeed);
        highSpeedCheckBox.addItemListener(highSpeedListener);

        addPositionView.setText(String.valueOf(ActionManager.global.getTaskSize()));

        buttonAddTask.setEnabled(isButtonEnabled());
        buttonAddSleepTask.setEnabled(isButtonEnabled());
        addPositionView.setEnabled(isButtonEnabled());
        countText.setEnabled(isButtonEnabled());
        jumpMoveCheckBox.setEnabled(isButtonEnabled());
        highSpeedCheckBox.setEnabled(isButtonEnabled());
        buttonSave.setEnabled(isButtonEnabled());
        buttonLoad.setEnabled(isButtonEnabled());

        flushPlayGlobalButton();

        if (includeList) {
            taskListView.removeAll();
            List<Task> tasks = ActionManager.global.copy();
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                JPanel jPanel = task.getSleepTime() > 0 ? getSleepItem(task, i) : getItem(task, i);
                taskListView.add(jPanel);
            }

            taskListView.revalidate();
            taskListView.repaint();
        }
    }

    private boolean couldPlayGlobal() {
        return isGameExists() && isButtonEnabled() && ActionManager.global.accessTaskSize() > 0;
    }

    private void flushPlayGlobalButton() {
        buttonPlayGlobal.setEnabled(couldPlayGlobal());
        Global global = ActionManager.global;
        buttonPlayGlobal.setText("执行所有任务(" + (global == null ? 0 : global.accessTaskSize()) + ")");
    }

    private void saveToFile() {
        if (ActionManager.global.getWidthWindow() == 0 || ActionManager.global.getHeightWindow() == 0) {
            if (WindowPositionManager.wGameWindow > 0) {
                ActionManager.global.setWidthWindow(WindowPositionManager.wGameWindow);
            }
            if (WindowPositionManager.hGameWindow > 0) {
                ActionManager.global.setHeightWindow(WindowPositionManager.hGameWindow);
            }
            saveToFile();
        }
        if (FileUtil.write(JsonUtil.toJson(ActionManager.global))) {
            S.s("保存成功");
        } else {
            S.e("保存失败");
        }
    }

    private void readFromFile(String filePath) {
        String json = FileUtil.read(filePath);
        ActionManager.global = JsonUtil.toGlobal(json);
    }

    private void play(Task task) {
        PlayManager.startPlay(task, new TaskExecutor.Callback() {
            @Override
            public void onRun(Task task, int count) {
                setTitle(getName(task) + " " + count + "/" + task.getCount());
                flushGlobalUI(true);
            }

            @Override
            public void onEnd() {
                setTitle("执行完毕");
                flushGlobalUI(true);
            }

            @Override
            public void onBreak(String quitMsg) {
                setTitle("执行中断：" + quitMsg);
                flushGlobalUI(true);
            }
        });
        flushGlobalUI(false);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        S.s(title);
    }

    public String getName(Task task) {
        return S.isEmpty(task.getName()) ? "任务" + task.id : task.getName();
    }

    public boolean isButtonEnabled() {
        return !isPlaying() && !isRecording();
    }

    public boolean isGameExists() {
        return WindowPositionManager.gameWindowExists;
    }

    public boolean isPlaying() {
        return PlayManager.isPlaying();
    }

    public boolean isRecording() {
        return RecordManager.isRecording();
    }

    interface Call {
        void initView();
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
