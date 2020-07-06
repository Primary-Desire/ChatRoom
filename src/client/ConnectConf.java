package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectConf extends JDialog {

    String userInputIp;
    int userInputPort;

    JPanel panelUserConf = new JPanel();
    JButton save = new JButton();
    JButton cancel = new JButton();
    JLabel dlgInfo = new JLabel(String.format("默认连接设置为: %s:%s", Client.ip, Client.port));

    JPanel panelSave = new JPanel();
    JLabel message = new JLabel();

    JTextField inputIp;
    JTextField inputPort;

    public ConnectConf(JFrame frame, String userInputIp, int userInputPort) {
        super(frame, true);
        this.userInputIp = userInputIp;
        this.userInputPort = userInputPort;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置运行时位置,使对话框居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 400) / 2 + 50, (screenSize.height - 600) / 2 + 150);
        this.setResizable(false);
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(300, 130));
        this.setTitle("连接设置");
        message.setText("请输入服务器的IP地址:");
        inputIp = new JTextField(10);
        inputIp.setText(userInputIp);
        inputPort = new JTextField(4);
        inputPort.setText(String.valueOf(userInputPort));
        save.setText("保存");
        cancel.setText("取消");

        panelUserConf.setLayout(new GridLayout(2, 2, 1, 1));
        panelUserConf.add(message);
        panelUserConf.add(inputIp);
        panelUserConf.add(new JLabel("请输入服务器的端口号:"));
        panelUserConf.add(inputPort);

        panelSave.add(new Label());
        panelSave.add(save);
        panelSave.add(cancel);
        panelSave.add(new Label());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panelUserConf, BorderLayout.NORTH);
        contentPane.add(dlgInfo, BorderLayout.CENTER);
        contentPane.add(panelSave, BorderLayout.SOUTH);

        // 保存按钮的事件处理
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int savePort;
                // 判断IP是否合法
                try {
                    userInputIp = InetAddress.getByName(inputIp.getText()).toString().substring(1);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    dlgInfo.setText("错误的IP地址!");
                    return;
                }
                // 判断端口是否合法
                try {
                    savePort = Integer.valueOf(inputPort.getText());
                    if (savePort < 1 || savePort > 65535) {
                        dlgInfo.setText("端口号必须是1-65535之间的整数!");
                        inputPort.setText("");
                        return;
                    }
                    userInputPort = savePort;
                    dispose();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    dlgInfo.setText("错误的端口号!端口号请填写整数!");
                    inputPort.setText("");
                    return;
                }
            }
        });

        // 关闭对话框时的操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dlgInfo.setText(String.format("默认的连接设置为: %s:%s", userInputIp, userInputPort));
            }
        });

        // 取消按钮的事件处理
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlgInfo.setText(String.format("默认的连接设置为: %s:%s", userInputIp, userInputPort));
                dispose();
            }
        });
    }

}
