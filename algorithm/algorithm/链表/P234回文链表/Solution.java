package DLeetcode.数据结构与算法.链表.P234回文链表;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    public static void main(String[] args) {

        ListNode listNode1 = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);

        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        Solution solution = new Solution();
        solution.isPalindrome1(listNode1);

    }
    public boolean isPalindrome(ListNode head){
        ListNode point = head;
        Deque<Integer> stack = new ArrayDeque<>();

        while (point != null){
            stack.push(point.val);
            point = point.next;
        }
        point = head;
        while (point != null){
            if (point.val != stack.pop()){
                return false;
            }
            point = point.next;
        }
        return true;
    }

    public boolean isPalindrome1(ListNode head){


        ListNode s = head, f = head;

        while (f != null && f.next != null) {
            s = s.next;
            f = f.next.next;
        }

        f = reverseList(s);
        ListNode temp2 = f;
        s = head;

        while (f != null){
            if (s.val != f.val){
                reverseList(temp2);
                return false;
            }
            s = s.next;
            f = f.next;
        }
        reverseList(temp2);
        return true;
    }

    public static ListNode reverseList(ListNode head){
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




class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
