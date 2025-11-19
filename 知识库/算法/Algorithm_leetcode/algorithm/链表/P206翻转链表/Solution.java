package DLeetcode.数据结构与算法.链表.P206翻转链表;

public class Solution {
    public static void main(String[] args) {

        int[] arr = new int[]{2};
        ListNode head = new ListNode(1);
        ListNode point = head;
        for (int i = 0; i < arr.length; i++) {
            point.next = new ListNode(arr[i]);
            point = point.next;
        }

        Solution solution = new Solution();
        solution.reverseList(head);


    }

    public ListNode reverseList(ListNode head) {
        if (head == null){
            return null;
        }

        ListNode before = head, after = head.next;
        before.next = null;

        while (after != null){
            ListNode temp = after.next;
            after.next = before;
            before = after;
            after = temp;
        }
        return before;
    }

    public ListNode reverseList1(ListNode head){
        ListNode prev = null, curr = head, next;
        while (curr != null){
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
class ListNode{
    int val;
    ListNode next;

    ListNode(int val){
        this.val = val;
    }
    ListNode(int val, ListNode next){
        this.val = val;
        this.next = next;
    }

    ListNode(){}
}
