package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new Client();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();
        if (object == userItem || object == userButton) { // 用户信息设置
            // 调出用户信息设置对话框
            UserConf userConf = new UserConf(this, userName);
            userConf.setVisible(true);
            userName = userConf.userInputName;
        } else if (object == connectItem || object == connectButton) { // 连接服务端设置
            // 调出连接设置对话框
            ConnectConf connectConf = new ConnectConf(this, ip, port);
            connectConf.setVisible(true);
            ip = connectConf.userInputIp;
            port = connectConf.userInputPort;
        } else if (object == loginItem || object == loginButton) { // 登录
            connect();
        } else if (object == logoutItem || object == logoutButton) { // 注销
            disconnect();
            showStatus.setText("");
        } else if (object == clientMessage || object == clientMessageButton) { // 发送消息
            sendMessage();
            clientMessage.setText("");
        } else if (object == exitItem || object == exitButton) { // 退出
            int define = JOptionPane.showConfirmDialog(this, "真的要退出吗?", "退出",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (define == JOptionPane.YES_OPTION) {
                if (type == 1) {
                    disconnect();
                }
                System.exit(0);
            }
        } else if (object == helpItem) { // 菜单栏中的帮助
            // 调出帮助对话框
            Help helpDialog = new Help(this);
            helpDialog.setVisible(true);
        }
    }

    private void sendMessage() {
        String toSomeBody = comboBox.getSelectedItem().toString();
        String status = null;
        if (checkBox.isSelected()) {
            status = "悄悄话";
        }
        Icon face = new ImageIcon("src/client/face/smile.gif");
        String action = actionList.getSelectedItem().toString();
        if (action.equals("微笑地")) {
            face = new ImageIcon("src/client/face/smile.gif");
        } else if (action.equals("高兴地")) {
            face = new ImageIcon("src/client/face/happy.gif");
        } else if (action.equals("轻轻地")) {
            face = new ImageIcon("src/client/face/quite.gif");
        } else if (action.equals("生气地")) {
            face = new ImageIcon("src/client/face/angry.gif");
        }

        express.setIcon(face);

        String message = clientMessage.getText();

        if (socket.isClosed()) {
            return;
        }

        try {
            output.writeObject("聊天信息");
            output.flush();
            output.writeObject(toSomeBody);
            output.flush();
            output.writeObject(status);
            output.flush();
            output.writeObject(action);
            output.flush();
            output.writeObject(message);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void connect() {
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(this, "不能连接到指定的服务器!\n请确认连接设置是否正确!", "提示",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(userName);
            output.flush();

            receiveThread = new ClientReceive(socket, output, input, comboBox, messageShow, showStatus);
            receiveThread.start();

            loginButton.setEnabled(false);
            loginItem.setEnabled(false);
            userButton.setEnabled(false);
            userItem.setEnabled(false);
            connectButton.setEnabled(false);
            connectItem.setEnabled(false);
            logoutButton.setEnabled(true);
            logoutItem.setEnabled(true);
            clientMessage.setEnabled(true);
            messageShow.append(String.format("连接服务器: %s:%s 成功...\n", ip, port));
            type = 1; // 标志位设为已连接
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    static String ip = "127.0.0.1"; // 服务端IP
    static int port = 1024; // 服务端侦听端口号
    public static String userName = GenerateChineseName.getChineseName(); // 用户名
    int type = 0; // 0 表示未连接, 1 表示已连接

    JComboBox comboBox; // 选择发送消息的接收者
    JTextArea messageShow; // 客户端的信息显示
    JScrollPane messageScrollPane; // 信息显示的滚动条

    JLabel express, sendToLabel, messageLabel;

    JTextField clientMessage; // 客户端消息的发送
    JCheckBox checkBox; // 悄悄话
    JComboBox actionList; // 表情选择
    JButton clientMessageButton; // 发送消息
    JTextField showStatus; // 显示用户连接状态

    Socket socket;
    ObjectOutputStream output; // 网络套接字输出流
    ObjectInputStream input; // 网络套接字输入流

    ClientReceive receiveThread;

    // 建立菜单栏
    JMenuBar menuBar = new JMenuBar();
    // 建立菜单组
    JMenu operateMenu = new JMenu("操作(O)");
    // 建立菜单项
    JMenuItem loginItem = new JMenuItem("用户登录(I)");
    JMenuItem logoutItem = new JMenuItem("用户注销(L)");
    JMenuItem exitItem = new JMenuItem("退出(X)");

    JMenu confMenu = new JMenu("设置(C)");
    JMenuItem userItem = new JMenuItem("用户设置(U)");
    JMenuItem connectItem = new JMenuItem("连接设置(C)");

    JMenu helpMenu = new JMenu("帮助(H)");
    JMenuItem helpItem = new JMenuItem("帮助(H)");

    // 建立工具栏
    JToolBar toolBar = new JToolBar();
    // 建立工具栏中的按钮
    JButton loginButton; // 用户登录
    JButton logoutButton; // 用户注销
    JButton userButton; // 用户信息的设置
    JButton connectButton; // 连接设置
    JButton exitButton; // 退出

    // 窗口大小
    Dimension faceSize = new Dimension(400, 600);

    JPanel downPanel;
    GridBagLayout gridBagLayout;
    GridBagConstraints gridBagConstraints;

    public Client() {
        init(); // 初始化程序

        // 添加窗口的关闭事件处理
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        // 设置窗口的大小
        this.setSize(faceSize);
        this.setVisible(true);

        setIconImage(getToolkit().getImage("src/client/face/love.gif"));

        // 设置运行时窗口的位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - faceSize.width) / 2, (screenSize.height - faceSize.height) / 2);
        this.setResizable(false);
        this.setTitle("聊天室客户端");

        operateMenu.setMnemonic('O');

        loginItem.setMnemonic('I');
        loginItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));

        logoutItem.setMnemonic('L');
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));

        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

        confMenu.setMnemonic('C');

        userItem.setMnemonic('U');
        userItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));

        connectItem.setMnemonic('C');
        connectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));

        helpMenu.setMnemonic('H');

        helpItem.setMnemonic('H');
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
    }

    /**
     * 程序初始化函数
     */
    private void init() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // 添加菜单栏
        operateMenu.add(loginItem);
        operateMenu.add(logoutItem);
        operateMenu.addSeparator();
        operateMenu.add(exitItem);
        menuBar.add(operateMenu);
        confMenu.add(userItem);
        confMenu.addSeparator();
        confMenu.add(connectItem);
        menuBar.add(confMenu);
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // 初始化按钮
        loginButton = new JButton("登录");
        logoutButton = new JButton("注销");
        userButton = new JButton("用户设置");
        connectButton = new JButton("连接设置");
        exitButton = new JButton("退出");
        // 鼠标悬停时显示信息
        loginButton.setToolTipText("连接到指定服务器");
        logoutButton.setToolTipText("与服务器断开连接");
        userButton.setToolTipText("设置用户信息");
        connectButton.setToolTipText("设置连接的服务器信息");
        // 将按钮添加到工具栏
        toolBar.add(userButton);
        toolBar.add(connectButton);
        toolBar.addSeparator();
        toolBar.add(loginButton);
        toolBar.add(logoutButton);
        toolBar.addSeparator();
        toolBar.add(exitButton);
        contentPane.add(toolBar, BorderLayout.NORTH);

        checkBox = new JCheckBox("悄悄话");
        checkBox.setSelected(false);

        actionList = new JComboBox();
        actionList.addItem("微笑地");
        actionList.addItem("高兴地");
        actionList.addItem("轻轻地");
        actionList.addItem("生气地");
        actionList.setSelectedIndex(0);

        //初始时
        loginButton.setEnabled(true);
        logoutButton.setEnabled(false);

        // 为菜单栏添加事件监听
        loginItem.addActionListener(this);
        logoutItem.addActionListener(this);
        exitItem.addActionListener(this);
        userItem.addActionListener(this);
        connectItem.addActionListener(this);
        helpItem.addActionListener(this);

        // 添加按钮的事件监听
        loginButton.addActionListener(this);
        logoutButton.addActionListener(this);
        userButton.addActionListener(this);
        connectButton.addActionListener(this);
        exitButton.addActionListener(this);

        comboBox = new JComboBox();
        comboBox.insertItemAt("所有人", 0);
        comboBox.setSelectedIndex(0);

        messageShow = new JTextArea();
        messageShow.setEditable(false);
        // 添加滚动条
        messageScrollPane = new JScrollPane(messageShow,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.setPreferredSize(new Dimension(400, 400));
        messageScrollPane.revalidate();

        clientMessage = new JTextField(23);
        clientMessage.setEnabled(false);
        clientMessageButton = new JButton();
        clientMessageButton.setText("发送");

        // 添加系统消息的事件监听
        clientMessage.addActionListener(this);
        clientMessageButton.addActionListener(this);

        sendToLabel = new JLabel("发送至");
        express = new JLabel("表情:");
        messageLabel = new JLabel("发送消息:");
        downPanel = new JPanel();
        gridBagLayout = new GridBagLayout();
        downPanel.setLayout(gridBagLayout);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        JLabel none = new JLabel();
        gridBagLayout.setConstraints(none, gridBagConstraints);
        downPanel.add(none);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(1, 0, 0, 0);
        // gridBagConstraints.ipadx = 5;
        // gridBagConstraints.ipady = 5;
        gridBagLayout.setConstraints(sendToLabel, gridBagConstraints);
        downPanel.add(sendToLabel);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagLayout.setConstraints(comboBox, gridBagConstraints);
        downPanel.add(comboBox);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagLayout.setConstraints(express, gridBagConstraints);
        downPanel.add(express);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        // gridBagConstraints.insets = new Insets(1, 0, 0, 0);
        // gridBagConstraints.ipadx = 5;
        // gridBagConstraints.ipady = 5;
        gridBagLayout.setConstraints(actionList, gridBagConstraints);
        downPanel.add(actionList);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(1, 0, 0, 0);
        // gridBagConstraints.ipadx = 5;
        // gridBagConstraints.ipady = 5;
        gridBagLayout.setConstraints(checkBox, gridBagConstraints);
        downPanel.add(checkBox);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagLayout.setConstraints(messageLabel, gridBagConstraints);
        downPanel.add(messageLabel);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(clientMessage, gridBagConstraints);
        downPanel.add(clientMessage);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagLayout.setConstraints(clientMessageButton, gridBagConstraints);
        downPanel.add(clientMessageButton);

        showStatus = new JTextField(35);
        showStatus.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagLayout.setConstraints(showStatus, gridBagConstraints);
        downPanel.add(showStatus);

        contentPane.add(messageScrollPane, BorderLayout.CENTER);
        contentPane.add(downPanel, BorderLayout.SOUTH);

        // 关闭程序时的操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (type == 1) {
                    disconnect();
                }
                System.exit(0);
            }
        });

    }

    private void disconnect() {
        loginButton.setEnabled(true);
        loginItem.setEnabled(true);
        userButton.setEnabled(true);
        userItem.setEnabled(true);
        connectButton.setEnabled(true);
        connectItem.setEnabled(true);
        logoutButton.setEnabled(false);
        logoutItem.setEnabled(false);
        clientMessage.setEnabled(false);

        if (socket.isClosed()) {
            return;
        }

        try {
            output.writeObject("用户下线");
            output.flush();

            input.close();
            output.close();
            socket.close();
            messageShow.append("已与服务器断开连接...\n");
            type = 0; // 标志位设为未连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
