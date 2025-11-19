package DLeetcode.数据结构与算法.链表.P92翻转链表2;

public class Solution {
    public static void main(String[] args) {

    }
    public ListNode reverseBetween(ListNode head, int left, int right){

        int l = 0, r = 0;

        ListNode lPoint = head, rPoint = head, dummy = new ListNode(-1);;
        dummy.next = head;
        head = dummy;

        while (rPoint != null){
            r++;
            rPoint = rPoint.next;

            if (l+1 != left){
                lPoint = lPoint.next;
                dummy = dummy.next;
                l++;
            }
            if (r == right){
                dummy.next = reverseList(lPoint, r - l);
                lPoint.next = rPoint;
            }
        }
        return head.next;
    }

    public static ListNode reverseList(ListNode head, int k){
        ListNode prev = null, curr = head, next;
        for (int i = 0; i < k; i++) {
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
    ListNode(){}
    ListNode(int val){
        this.val = val;
    }
    ListNode(int val, ListNode next){
        this.val = val;
        this.next = next;
    }
}
