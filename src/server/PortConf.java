package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PortConf extends JDialog {

    JPanel panelPort = new JPanel();
    JButton save = new JButton();
    JButton cancel = new JButton();
    public static JLabel dlgInfo = new JLabel(String.format("默认端口号为: %s", Server.port));

    JPanel panelSave = new JPanel();
    JLabel message = new JLabel();

    public static JTextField portNumber;

    public PortConf(JFrame frame) {
        super(frame, true);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置运行位置,使对话框居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 400) / 2 + 50, (screenSize.height - 600) / 2 + 150);
        this.setResizable(false);
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(300, 120));
        this.setTitle("端口设置");
        message.setText("请输入侦听的端口号:");
        portNumber = new JTextField(10);
        portNumber.setText(String.valueOf(Server.port));
        save.setText("保存");
        cancel.setText("取消");

        panelPort.setLayout(new FlowLayout());
        panelPort.add(message);
        panelPort.add(portNumber);

        panelSave.add(new Label());
        panelSave.add(save);
        panelSave.add(cancel);
        panelSave.add(new Label());

        Container contentPanel = getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(panelPort, BorderLayout.NORTH);
        contentPanel.add(dlgInfo, BorderLayout.CENTER);
        contentPanel.add(panelSave, BorderLayout.SOUTH);

        // 保存按钮的事件处理
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int savePort;
                try {
                    savePort = Integer.parseInt(PortConf.portNumber.getText());
                    if (savePort < 1 || savePort > 65535) {
                        PortConf.dlgInfo.setText("侦听端口必须是1-65535之间的整数");
                        PortConf.portNumber.setText("");
                        return;
                    }
                    Server.port = savePort;
                    dispose();
                } catch (NumberFormatException e) {
                    PortConf.dlgInfo.setText("错误的端口号, 端口号请填写整数");
                    PortConf.portNumber.setText("");
                    return;
                }
            }
        });

        // 关闭对话框时的操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dlgInfo.setText(String.format("默认端口号为:%s", Server.port));
            }
        });

        // 取消按钮的事件处理
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlgInfo.setText(String.format("默认端口号为%s",Server.port));
                dispose();
            }
        });
    }

}
