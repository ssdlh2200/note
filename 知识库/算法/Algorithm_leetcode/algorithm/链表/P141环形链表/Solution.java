package DLeetcode.数据结构与算法.链表.P141环形链表;


import java.util.HashMap;

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
}

public class Solution {
    public static void main(String[] args) {

    }

    public boolean hasCycle0(ListNode head) {
        ListNode point = head;
        HashMap<ListNode, Boolean> map = new HashMap<>();
        while (point != null){
            if (map.containsKey(point)){
                return true;
            } else{
                map.put(point, true);
            }
            point = point.next;
        }
        return false;
    }
    public boolean hasCycle(ListNode head){

        ListNode s = head, f = head;
        while (f != null && f.next != null){
            f = f.next.next;
            s = s.next;
            if (s == f){
                return true;
            }
        }
        return false;
    }
}
