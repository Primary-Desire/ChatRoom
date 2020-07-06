package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Help extends JDialog {

    JPanel titlePanel = new JPanel();
    JPanel contentPanel = new JPanel();
    JPanel closePanel = new JPanel();

    JButton close = new JButton();
    JLabel title = new JLabel("聊天室客户端帮助");
    JTextArea help = new JTextArea();

    Color backgroundColor = new Color(255, 255, 255);

    public Help(JFrame frame) {
        super(frame, true);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置运行时位置,使对话框居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 400) / 2 + 25, (screenSize.height - 320) / 2);
        this.setResizable(false);
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(350, 270));
        this.setTitle("帮助");

        titlePanel.setBackground(backgroundColor);
        contentPanel.setBackground(backgroundColor);
        closePanel.setBackground(backgroundColor);

        StringBuilder helpText = new StringBuilder(String.format("1. 设置需要连接的服务端IP地址和端口(默认设置为\n%s:%s);", Client.ip, Client.port));
        helpText.append(String.format("2. 输入您的用户名(默认设置为:%s);", Client.userName));
        helpText.append("3. 点击'登录'便可以连接到指定服务器,\n点击'注销'可以和服务器端开连接;\n");
        helpText.append("4. 选择需要接受消息的用户,在消息栏中写入消息,\n同时选择表情,之后便可发送消息.\n");
        help.setText(helpText.toString());

        help.setEditable(false);

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
