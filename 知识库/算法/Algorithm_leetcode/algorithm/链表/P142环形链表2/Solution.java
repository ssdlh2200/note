package DLeetcode.数据结构与算法.链表.P142环形链表2;

public class Solution {
    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(3);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(0);
        ListNode listNode4 = new ListNode(-4);


        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode2;

        Solution solution = new Solution();
        solution.detectCycle(listNode1);

    }

    public ListNode detectCycle(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null){
            s = s.next;
            f = f.next.next;
            if (s == f){
                f = head;
                while (s != f){
                    s = s.next;
                    f = f.next;
                }
                return s;
            }
        }
        return null;
    }
}
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
}

