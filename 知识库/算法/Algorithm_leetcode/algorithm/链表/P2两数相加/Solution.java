package DLeetcode.数据结构与算法.链表.P2两数相加;


class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        int c = 0, a = 0, b = 0;
        ListNode ans = new ListNode(-1);
        ListNode dummy = ans;
        while (l1 != null || l2!=null){
            if (l1 == null){
                a = 0;
            } else {
                a = l1.val;
                l1 = l1.next;
            }
            if (l2 == null){
                b = 0;
            } else {
                b = l2.val;
                l2 = l2.next;
            }

            int dig = a + b + c;

            if (dig > 9){
                dig = dig % 10;
                c = 1;
            } else {
                c = 0;
            }
            ans.next = new ListNode(dig);
            ans = ans.next;
        }
        if (c != 0){
            ans.next = new ListNode(c);
        }
        return dummy.next;
    }
}