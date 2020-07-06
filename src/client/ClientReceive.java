package client;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientReceive extends Thread {
    private JComboBox comboBox;
    private JTextArea textArea;

    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;
    JTextField showStatus;

    public ClientReceive(Socket socket, ObjectOutputStream output, ObjectInputStream input,
                         JComboBox comboBox, JTextArea textArea, JTextField showStatus) {
        this.socket = socket;
        this.output = output;
        this.input = input;
        this.comboBox = comboBox;
        this.textArea = textArea;
        this.showStatus = showStatus;
    }

    @Override
    public void run() {
        super.run();
        while (!socket.isClosed()) {
            try {
                String type = String.valueOf(input.readObject());
                if (type.equalsIgnoreCase("系统信息")) {
                    String sysMsg = String.valueOf(input.readObject());
                    textArea.append(String.format("系统信息: %s", sysMsg));
                } else if (type.equalsIgnoreCase("服务关闭")) {
                    output.close();
                    input.close();
                    socket.close();

                    textArea.append("服务器已关闭!\n");

                    break;
                } else if (type.equalsIgnoreCase("聊天信息")) {
                    String message = String.valueOf(input.readObject());
                    textArea.append(message);
                } else if (type.equalsIgnoreCase("用户列表")) {
                    String userList = String.valueOf(input.readObject());
                    String[] userNameArray = userList.split("\n");
                    comboBox.removeAllItems();

                    int i = 0;
                    comboBox.addItem("所有人");
                    while (i < userNameArray.length) {
                        comboBox.addItem(userNameArray[i]);
                        i ++;
                    }
                    comboBox.setSelectedIndex(0);
                    showStatus.setText(String.format("在线用户%s人", userNameArray.length));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
