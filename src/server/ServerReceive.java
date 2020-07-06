package server;

import javax.swing.*;

/**
 * 服务端收发消息的类
 */
public class ServerReceive extends Thread {

    JTextArea textArea; // 服务端的信息显示
    JTextField textField; // 显示用户连接状态
    JComboBox comboBox; // 选择发送消息的接收者

    Node client;
    UserLinkList userLinkList; // 用户链表

    public boolean isStop;

    public ServerReceive(JTextArea textArea, JTextField textField, JComboBox comboBox, Node client, UserLinkList userLinkList) {
        this.textArea = textArea;
        this.textField = textField;
        this.comboBox = comboBox;
        this.client = client;
        this.userLinkList = userLinkList;

        isStop = false;
    }

    @Override
    public void run() {
        super.run();
        // 向所有人发送用户的列表
        sendUserList();

        while (!isStop && !client.socket.isClosed()) {
            try {
                String type = String.valueOf(client.input.readObject());

                if (type.equalsIgnoreCase("聊天信息")) {
                    String toSomeBody = String.valueOf(client.input.readObject());
                    String status = String.valueOf(client.input.readObject());
                    String action = String.valueOf(client.input.readObject());
                    String message = String.valueOf(client.input.readObject());

                    StringBuilder msg = new StringBuilder(client.userName);
                    msg.append(" ");
                    msg.append(action);
                    msg.append("对 ");
                    msg.append(toSomeBody);
                    msg.append(" 说: ");
                    msg.append(message);
                    msg.append("\n");

                    if (status.equalsIgnoreCase("悄悄话")) {
                        msg.insert(0, " [悄悄话]");
                    }
                    textArea.append(msg.toString());

                    if (toSomeBody.equalsIgnoreCase("所有人")) {
                        sendToAll(msg.toString()); // 向所有人发送消息
                    } else {
                        try {
                            client.output.writeObject("聊天信息"); // 将指定的对象写入ObjectOutputStream
                            client.output.flush();
                            client.output.writeObject(msg.toString());
                            client.output.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Node node = userLinkList.findUser(toSomeBody);
                        if (node != null) {
                            node.output.writeObject("聊天信息");
                            node.output.flush();
                            node.output.writeObject(msg.toString());
                            node.output.flush();
                        }
                    }
                } else if (type.equalsIgnoreCase("用户下线")) {
                    Node node = userLinkList.findUser(client.userName);
                    userLinkList.delUser(node);

                    StringBuilder msg = new StringBuilder("用户 ").append(client.userName).append(" 下线\n");
                    int count = userLinkList.getCount();

                    comboBox.removeAllItems(); // 从项列表中移除所有项
                    comboBox.addItem("所有人");

                    int i = 0;
                    while (i < count) {
                        node = userLinkList.findUser(i);
                        if (node == null) {
                            i ++;
                            continue;
                        }
                        comboBox.addItem(node.userName);
                        i ++;
                    }
                    comboBox.setSelectedIndex(0);

                    textArea.append(msg.toString());
                    textField.setText(String.format("在线用户%s人\n", userLinkList.getCount()));

                    sendToAll(msg.toString());
                    sendUserList();

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToAll(String msg) {
        int count = userLinkList.getCount();

        int i = 0;
        while (i < count) {
            Node node = userLinkList.findUser(i);
            if (node == null) {
                i ++;
                continue;
            }
            try {
                node.output.writeObject("聊天信息");
                node.output.flush();
                node.output.writeObject(msg);
                node.output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i ++;
        }
    }

    /**
     * 向所有人发送用户的列表
     */
    private void sendUserList() {
        String userList = "";
        int count = userLinkList.getCount();

        int i = 0;
        while (i < count) {
            Node node = userLinkList.findUser(i);
            if (node == null) {
                i ++;
                continue;
            }
            userList += node.userName;
            userList += "\n";
            i ++;
        }

        i = 0;
        while (i < count) {
            Node node = userLinkList.findUser(i);
            if (node == null) {
                i ++;
                continue;
            }
            try {
                node.output.writeObject("用户列表");
                node.output.flush();
                node.output.writeObject(userList);
                node.output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i ++;
        }
    }
}
