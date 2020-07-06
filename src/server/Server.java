package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;

public class Server extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new Server();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        if (object == startServer || object == startItem) { // 启动服务端
            startService();
        } else if (object == stopServer || object == stopItem) { // 停止服务端
            int define = JOptionPane.showConfirmDialog(this, "真的停止服务吗?", "停止服务",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (define == JOptionPane.YES_OPTION) {
                stopService();
            }
        } else if (object == portSet || object == portItem) { // 端口设置
            // 调出端口设置的对话框
            PortConf portConf = new PortConf(this);
            portConf.show();
        } else if (object == exitButton || object == exitItem) { // 退出程序
            int define = JOptionPane.showConfirmDialog(this, "真的要退出吗?", "退出",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (define == JOptionPane.YES_OPTION) {
                stopService();
                System.exit(0);
            }
        } else if (object == helpItem) { // 菜单栏中的帮助
            // 调出帮助对话框
            Help helpDialog = new Help(this);
            helpDialog.show();
        } else if (object == sysMessage || object == sysMessageButton) { // 发送系统消息
            sendSystemMessage();
        }
    }

    /**
     * 向客户端用户发送消息
     */
    private void sendSystemMessage() {
        String toSomeBody = comboBox.getSelectedItem().toString();
        String message = sysMessage.getText() + "\n";

        messageShow.append(message);

        // 向所有人发送消息
        if (toSomeBody.equalsIgnoreCase("所有人")) {
            sendMsgToAll(message);
        } else {
            // 向某个用户发送消息
            Node node = userLinkList.findUser(toSomeBody);
            try {
                node.output.writeObject("系统信息");
                node.output.flush();
                node.output.writeObject(message);
                node.output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sysMessage.setText("");
        }
    }

    /**
     * @param message
     * 向所有人发送消息
     */
    private void sendMsgToAll(String message) {
        int count = userLinkList.getCount();
        int i = 0;
        while (i < count) {
            Node node = userLinkList.findUser(i);
            if (node == null) {
                i ++;
                continue;
            }
            // 即使没有人服务器仍然可以发送消息
            try {
                node.output.writeObject("系统消息");
                node.output.flush(); // 刷新此输出流并强制写出所有缓冲的输出字节
                node.output.writeObject(message);
                node.output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i ++;
        }
        sysMessage.setText("");
    }

    private void startService() {
        try {
            serverSocket = new ServerSocket(port);
            messageShow.append(String.format("服务端已经启动,在%s端口侦听...\n", port));

            startServer.setEnabled(false);
            startItem.setEnabled(false);
            portSet.setEnabled(false);
            portItem.setEnabled(false);

            stopServer.setEnabled(true);
            stopItem.setEnabled(true);
            sysMessage.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 多用户的监听设置
        userLinkList = new UserLinkList();
        listenThread = new ServerListen(serverSocket, comboBox, messageShow, showStatus, userLinkList);
        listenThread.start();
    }

    public static int port = 1024; // 服务端监听端口

    ServerSocket serverSocket; // 服务的Socket
    JComboBox comboBox; // 选择发送消息的接收者
    JTextArea messageShow; // 服务端的信息显示
    JScrollPane messageScrollPane; // 信息显示的滚动条
    JTextField showStatus; // 显示用户的连接状态
    JLabel sendToLabel, messageLabel;
    JTextField sysMessage; // 服务端消息的发送
    JButton sysMessageButton; // 服务端消息的发送按钮
    UserLinkList userLinkList; // 用户链表

    // 建立菜单栏
    JMenuBar menuBar = new JMenuBar();
    // 建立菜单组
    JMenu serviceMenu = new JMenu("服务(V)");
    // 建立菜单项
    JMenuItem portItem = new JMenuItem("端口设置(P)");
    JMenuItem startItem = new JMenuItem("启动服务(S)");
    JMenuItem stopItem = new JMenuItem("停止服务(T)");
    JMenuItem exitItem = new JMenuItem("退出(X)");

    JMenu helpMenu = new JMenu("帮助(H)");
    JMenuItem helpItem = new JMenuItem("帮助(H)");

    // 建立工具栏
    JToolBar toolBar = new JToolBar();

    // 建立工具栏中的按钮
    JButton portSet; // 启动服务端端口设置
    JButton startServer; // 启动服务端
    JButton stopServer; // 停止服务端
    JButton exitButton; // 退出按钮

    // 窗口大小
    Dimension faceSize = new Dimension(400, 600);

    ServerListen listenThread; // 用户多线程

    JPanel downPanel;
    GridBagLayout gridBagLayout;
    GridBagConstraints gridBagConstraints;

    /**
     * 服务端构造函数
     */
    public Server() {
        init(); // 初始化程序
        // 添加窗口的关闭事件处理
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        // 设置窗口的大小
        this.setSize(faceSize);
        this.setVisible(true);
        // 设置运行时窗口的位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - faceSize.width) / 2, (screenSize.height - faceSize.height) / 2);
        this.setResizable(false);
        this.setTitle("聊天室服务端"); // 设置标题

        // 服务菜单快捷键 'V'
        serviceMenu.setMnemonic('V');

        // 端口设置快捷键为 'ctrl' + 'p'
        portItem.setMnemonic('P');
        portItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));

        // 启动服务快捷键 'ctrl' + 's'
        startItem.setMnemonic('S');
        startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        // 停止服务快捷键 'ctrl' + 't'
        stopItem.setMnemonic('T');
        stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));

        // 退出快捷键 'ctrl' + 'x'
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

        // 帮助菜单栏快捷键 'H'
        helpMenu.setMnemonic('H');

        // 帮助设置快捷键 'ctrl' + 'h'
        helpItem.setMnemonic('H');
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
    }

    private void init() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // 添加菜单栏
        serviceMenu.add(portItem);
        serviceMenu.add(startItem);
        serviceMenu.addSeparator(); // 添加分隔栏
        serviceMenu.add(stopItem);
        serviceMenu.addSeparator();
        serviceMenu.add(exitItem);
        menuBar.add(serviceMenu);
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // 初始化按钮
        portSet = new JButton("端口设置");
        startServer = new JButton("启动服务");
        stopServer = new JButton("停止服务");
        exitButton = new JButton("退出");
        // 将按钮添加到工具栏
        toolBar.add(portSet);
        toolBar.addSeparator(); // 添加分隔栏
        toolBar.add(startServer);
        toolBar.add(stopServer);
        toolBar.addSeparator();
        toolBar.add(exitButton);
        contentPane.add(toolBar, BorderLayout.NORTH);

        // 初始时,令停止服务按钮不可用
        stopServer.setEnabled(false);
        stopItem.setEnabled(false);

        // 为菜单栏添加事件监听
        portItem.addActionListener(this);
        startItem.addActionListener(this);
        stopItem.addActionListener(this);
        exitItem.addActionListener(this);
        helpItem.addActionListener(this);

        // 添加按钮的事件监听
        portSet.addActionListener(this);
        startServer.addActionListener(this);
        stopServer.addActionListener(this);
        exitButton.addActionListener(this);

        comboBox = new JComboBox();
        comboBox.insertItemAt("所有人", 0);
        comboBox.setSelectedIndex(0);

        messageShow = new JTextArea();
        messageShow.setEditable(false);

        // 添加滚动条
        messageScrollPane = new JScrollPane(messageShow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.setPreferredSize(new Dimension(400, 400));
        messageScrollPane.revalidate();

        showStatus = new JTextField(35);
        showStatus.setEditable(false);

        sysMessage = new JTextField(24);
        sysMessage.setEnabled(false);
        sysMessageButton = new JButton();
        sysMessageButton.setText("发送");

        // 添加系统消息的事件监听
        sysMessage.addActionListener(this);
        sysMessageButton.addActionListener(this);

        sendToLabel = new JLabel("发送至:");
        messageLabel = new JLabel("发送消息:");
        downPanel = new JPanel();
        gridBagLayout = new GridBagLayout();
        downPanel.setLayout(gridBagLayout);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0; // 指定包含组件的显示区域开始边的单元格,其中行的第一个单元格为 gridx = 0
        gridBagConstraints.gridy = 0; // 指定位于组件显示区域的顶部的单元格,其中最上边的单元格为 gridy = 0
        gridBagConstraints.gridwidth = 3; // 指定在组件显示区域的一行中的单元格数
        gridBagConstraints.gridheight = 2; // 指定在组件显示区域的一列中的单元格数
        gridBagConstraints.ipadx = 5; // 此字段指定组件的内部填充,即给组件的最小宽度添加多大的空间
        gridBagConstraints.ipady = 5; // 此字段指定内部填充,即给组件的最小高度添加多大的空间
        JLabel none = new JLabel("  ");
        gridBagLayout.setConstraints(none, gridBagConstraints);
        downPanel.add(none);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(1, 0, 0, 0);
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagLayout.setConstraints(sendToLabel, gridBagConstraints);
        downPanel.add(sendToLabel);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START; // 当组件小于其显示区域时使用此字段
        gridBagLayout.setConstraints(comboBox, gridBagConstraints);
        downPanel.add(comboBox);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagLayout.setConstraints(messageLabel, gridBagConstraints);
        downPanel.add(messageLabel);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagLayout.setConstraints(sysMessage, gridBagConstraints);
        downPanel.add(sysMessage);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagLayout.setConstraints(sysMessageButton, gridBagConstraints);
        downPanel.add(sysMessageButton);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagLayout.setConstraints(showStatus, gridBagConstraints);
        downPanel.add(showStatus);

        contentPane.add(messageScrollPane, BorderLayout.CENTER);
        contentPane.add(downPanel, BorderLayout.SOUTH);
        // 包含关系: contentPane >> downPanel >> gridBagLayout >> gridBagConstraints

        // 关闭程序时的操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                stopService();
                System.exit(0);
            }
        });
    }

    /**
     * 关闭服务端
     */
    private void stopService() {
        try {
            // 向所有人发送服务器关闭的消息
            sendStopToAll();
            listenThread.isStop = true;
            serverSocket.close();

            // 当前用户总数
            int count = userLinkList.getCount();

            int i = 0;
            while (i < count) {
                Node node = userLinkList.findUser(i);

                node.input.close();
                node.output.close();
                node.socket.close();

                i ++;
            }

            stopServer.setEnabled(false);
            stopItem.setEnabled(false);
            startServer.setEnabled(true);
            startItem.setEnabled(true);
            portSet.setEnabled(true);
            portItem.setEnabled(true);
            sysMessage.setEnabled(false);

            messageShow.append("服务端已关闭\n");

            comboBox.removeAllItems();
            comboBox.addItem("所有人");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendStopToAll() {
        int count = userLinkList.getCount();
        int i = 0;
        while (i < count) {
            Node node = userLinkList.findUser(i);
            if (node == null) {
                i ++;
                continue;
            }
            // 即使没有人服务器仍然可以发送消息
            try {
                node.output.writeObject("服务关闭");
                node.output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i ++;
        }
    }

}
