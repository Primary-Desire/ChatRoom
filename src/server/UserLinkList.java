package server;

/**
 * 用户链表
 */
public class UserLinkList {

    Node root;
    Node pointer;
    int count;

    /**
     * 构建用户链表
     */
    public UserLinkList() {
        root = new Node();
        root.next = null;
        pointer = null;
        count = 0;
    }

    /**
     * @param node
     * 添加用户
     */
    public void addUser(Node node) {
        pointer = root;

        while (pointer.next != null) {
            pointer = pointer.next;
        }

        pointer.next = node;
        node.next = null;
        count ++;
    }

    /**
     * @param node
     * 删除用户
     */
    public void delUser(Node node) {
        pointer = root;

        while (pointer.next != null) {
            if (pointer.next == node) {
                pointer.next = node.next;
                count --;

                break;
            }
            pointer = pointer.next;
        }
    }

    /**
     * @return
     * 返回用户数
     */
    public int getCount() {
        return count;
    }

    /**
     * @param userName
     * @return
     * 根据用户名查找用户
     */
    public Node findUser(String userName) {
        if (count == 0) {
            return null;
        }

        pointer = root;

        while (pointer.next != null) {
            pointer = pointer.next;
            if (pointer.userName.equalsIgnoreCase(userName)) {
                return pointer;
            }
        }

        return null;
    }

    /**
     * @param index
     * @return
     * 根据索引查找用户
     */
    public Node findUser(int index) {
        if (count == 0) {
            return null;
        }

        if (index < 0) {
            return null;
        }

        pointer = root;

        int i = 0;
        while (i < index + 1) {
            if (pointer.next != null) {
                pointer = pointer.next;
            } else {
                return null;
            }
            i ++;
        }

        return pointer;
    }

}
