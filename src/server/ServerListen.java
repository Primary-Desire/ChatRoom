package server;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

/**
 * 服务端的侦听类
 */
public class ServerListen extends Thread {

    ServerSocket serverSocket;

    JComboBox comboBox; // 选择发送消息的接收者
    JTextArea textArea; // 服务端的信息显示
    JTextField textField; // 显示用户连接状态
    UserLinkList userLinkList; // 用户链表

    Node client;
    ServerReceive receiveThread;

    public boolean isStop;

    public ServerListen(ServerSocket serverSocket, JComboBox comboBox,
                        JTextArea textArea, JTextField textField, UserLinkList userLinkList) {
        this.serverSocket = serverSocket;
        this.comboBox = comboBox;
        this.textArea = textArea;
        this.textField = textField;
        this.userLinkList = userLinkList;

        isStop = false;
    }

    @Override
    public void run() {
        super.run();
        while (!isStop && !serverSocket.isClosed()) { // 返回ServerSocket的关闭状态
            try {
                client = new Node();
                client.socket = serverSocket.accept(); // 监听用户
                client.output = new ObjectOutputStream(client.socket.getOutputStream());
                client.output.flush();
                client.input = new ObjectInputStream(client.socket.getInputStream());
                client.userName = String.valueOf(client.input.readObject()); // 从ObjectInputStream读取对象

                // 显示提示信息
                comboBox.addItem(client.userName);
                userLinkList.addUser(client);
                textArea.append(String.format("用户%s上线\n", client.userName));
                textField.setText(String.format("在线用户%s人\n", userLinkList.getCount()));

                receiveThread = new ServerReceive(textArea, textField, comboBox, client, userLinkList);
                receiveThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
