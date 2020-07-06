package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Help extends JDialog {

    JPanel titlePanel = new JPanel();
    JPanel contentPanel = new JPanel();
    JPanel closePanel = new JPanel();

    JButton close = new JButton();
    JLabel title = new JLabel("聊天室服务端帮助");
    JTextArea help = new JTextArea();

    Color backgroundColor = new Color(255, 255, 255);

    public Help(JFrame frame) {
        super(frame, true);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置运行位置,使对话框居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 400) / 2, (screenSize.height - 320) / 2);
        this.setResizable(false);
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(400, 200));
        this.setTitle("帮助");

        titlePanel.setBackground(backgroundColor);
        contentPanel.setBackground(backgroundColor);
        closePanel.setBackground(backgroundColor);

        StringBuilder helpText = new StringBuilder(String.format("1. 设置服务端的侦听端口(默认端口为%s);\n", Server.port));
        helpText.append("2. 点击'启动服务'按钮便可在指定的端口启动服务;\n");
        helpText.append("3. 选择需要接收消息的用户,在消息栏中写入消息,之后便可发送消息;\n");
        helpText.append("4. 信息状态栏中显示服务器当前的启动与停止状态、用户发送的消息和\n服务端发送的系统消息.");

        help.setText(helpText.toString());

        titlePanel.add(new Label());
        titlePanel.add(title);
        titlePanel.add(new Label());

        contentPanel.add(help);

        closePanel.add(new Label());
        closePanel.add(close);
        closePanel.add(new Label());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(titlePanel, BorderLayout.NORTH);
        contentPane.add(contentPanel, BorderLayout.CENTER);
        contentPane.add(closePanel, BorderLayout.SOUTH);

        close.setText("关闭");
        // 事件处理
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

}
