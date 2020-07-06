package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserConf extends JDialog {

    JPanel panelUserConf = new JPanel();
    JButton save = new JButton();
    JButton cancel = new JButton();
    JLabel dlgInfo = new JLabel(String.format("默认用户名为: %s", Client.userName));

    JPanel panelSave = new JPanel();
    JLabel message = new JLabel();
    String userInputName;

    JTextField userName;

    public UserConf(JFrame frame, String userInputName) {
        super(frame, true);
        this.userInputName = userInputName;
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
        this.setSize(new Dimension(300, 120));
        this.setTitle("用户设置");
        userName = new JTextField(10);
        userName.setText(userInputName);
        save.setText("保存");
        cancel.setText("取消");

        panelUserConf.setLayout(new FlowLayout());
        panelUserConf.add(message);
        panelUserConf.add(userName);

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
            public void actionPerformed(ActionEvent e) {
                if (userName.getText().equals("")) {
                    dlgInfo.setText("用户名不能为空!");
                    userName.setText(userInputName);
                    return;
                } else if (userName.getText().length() > 15) {
                    dlgInfo.setText("用户名长度不能大于15个字符!");
                    userName.setText(userInputName);
                    return;
                }
                userInputName = userName.getText();
                dispose();
            }
        });

        // 关闭对话框时的操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dlgInfo.setText(String.format("默认用户名: %s", Client.userName));
            }
        });

        // 取消按钮的事件处理
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlgInfo.setText(String.format("默认用户名: %s", Client.userName));
                dispose();
            }
        });
    }

}
