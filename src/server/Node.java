package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 用户链表的节点类
 */
public class Node {

    String userName = null;
    Socket socket = null;
    ObjectOutputStream output = null;
    ObjectInputStream input = null;

    Node next = null;

}
