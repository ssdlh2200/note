package DLeetcode.数据结构与算法.链表.P83删除排序链表中的重复元素;


class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}


class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null){
            return null;
        }
        ListNode l = head, r = head.next;
        while(r != null){
            while(r != null && l.val == r.val){
                r=r.next;
            }
            l.next = r;
            l=r;
        }
        return head;
    }
    public ListNode deleteDuplicates1(ListNode head){
        ListNode cur = head;
        while (cur != null) {
            if (cur.next != null && cur.next.val == cur.val){
                cur.next = cur.next.next;
            } else {
                cur = cur.next;
            }
        }
        return head;
    }
}