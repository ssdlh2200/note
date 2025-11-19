package DLeetcode.数据结构与算法.链表.P19删除链表的倒数第N个结点;

public class Solution {
    public static void main(String[] args) {

        Solution solution = new Solution();
        ListNode list = new ListNode(1);
        solution.removeNthFromEnd(list, 1);
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {

        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode l = dummy, r = head;

        for (int i = 0; i < n; i++) {
            r = r.next;
        }

        while (r != null){
            l = l.next;
            r = r.next;
        }
        l.next = l.next.next;

        return dummy.next;
    }
}


class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
