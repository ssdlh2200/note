package DLeetcode.数据结构与算法.链表.剑指Offer52两个链表的第一个公共节点;

import java.util.HashSet;

public class Solution {

    ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        int aLen = 0, bLen = 0;

        ListNode pointA = headA, pointB = headB;

        while (pointA != null){
            aLen++;
            pointA = pointA.next;
        }
        while (pointB != null){
            bLen++;
            pointB = pointB.next;
        }
        if (pointA != pointB){
            return null;
        }

        int dif = Math.abs(aLen - bLen);
        if (aLen >= bLen){
            pointA = headA;
            pointB = headB;
        } else {
            pointA = headB;
            pointB = headA;
        }
        for (int i = 0; i < dif; i++) {
            pointA = pointA.next;
        }
        while (pointA != pointB){
            pointA = pointA.next;
            pointB = pointB.next;
        }
        return pointA;
    }
    /*容器的办法*/
    ListNode getIntersectionNode1(ListNode headA, ListNode headB) {

        HashSet<ListNode> set = new HashSet<>();
        ListNode pointA = headA, pointB = headB;

        while (pointA != null){
            set.add(pointA);
            pointA = pointA.next;
        }
        while (pointB != null){
            if (set.contains(pointB)){
                return pointB;
            }
            pointB = pointB.next;
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
