package DLeetcode.数据结构与算法.链表.P138复制带随机指针的链表;

import java.util.HashMap;

public class Solution {
    public static void main(String[] args) {

    }


    /*容器解法*/
    public Node copyRandomList(Node head) {
        if (head == null){
            return null;
        }
        Node oldHeadPoint = head, dummy = new Node(-1);
        Node newHeadPoint = dummy;
        HashMap<Node, Node> map = new HashMap<>();

        while (oldHeadPoint != null){
            Node newNode = new Node(oldHeadPoint.val);
            newHeadPoint.next = newNode;
            newHeadPoint = newNode;

            map.put(oldHeadPoint, newNode);

            oldHeadPoint = oldHeadPoint.next;
        }
        oldHeadPoint = head;
        newHeadPoint = dummy.next;

        while (oldHeadPoint != null){
            newHeadPoint.random = map.get(oldHeadPoint.random);
            newHeadPoint = newHeadPoint.next;
            oldHeadPoint = oldHeadPoint.next;
        }
        return dummy.next;
    }


    /*非容器解法*/
    public Node copyRandomList1(Node head){

        Node old = head, dummy = new Node(-1);
        Node point = dummy;

        /*老链表插入新结点*/
        while (old != null){
            Node next = old.next;
            Node toAdd = new Node(old.val);
            old.next = toAdd;
            toAdd.next = next;
            old = next;
        }
        old = head;
        /*两个两个遍历*/
        while (old != null){
            if (old.random!=null){
                old.next.random = old.random.next;
            } else {
                old.next.random = null;
            }
            old = old.next.next;
        }
        old = head;
        /*拿出新链表, 原来老链表*/
        while (old != null){
            point.next = old.next;
            old.next = old.next.next;

            old  = old.next;
            point = point.next;
        }
        return dummy.next;
    }

}

// Definition for a Node.
class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}
