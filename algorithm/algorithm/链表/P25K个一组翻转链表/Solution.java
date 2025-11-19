package DLeetcode.数据结构与算法.链表.P25K个一组翻转链表;

public class Solution {
    public static void main(String[] args) {

        int[] arr = new int[]{1,2,3,4,5};
        ListNode head = new ListNode(-1);
        ListNode point = head;
        for (int i = 0; i < arr.length; i++) {
            point.next = new ListNode(arr[i]);
            point = point.next;
        }

        Solution solution = new Solution();
        solution.reverseKGroup(head.next, 1);
    }
    public ListNode reverseKGroup(ListNode head, int k) {
        int l = 0, r = 0, flag = 1;
        ListNode lPoint = head, rPoint = head, lastNode = new ListNode(-1);

        while (rPoint != null) {
            rPoint = rPoint.next;
            r++;
            if ( (r - l) % k == 0){
                ListNode first = reverseList(lPoint, k);
                if (flag-- == 1){
                    head = first;
                }
                lastNode.next = first;
                lPoint.next = rPoint;
                lastNode = lPoint;
                lPoint = rPoint;
                l = r;
            }
        }
        return head;
    }






    /*prev：代表链表的第一个节点, head ：代表链表的最后一个节点*/
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
