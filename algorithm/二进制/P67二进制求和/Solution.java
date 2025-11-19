package com.ssdlh.algorithm.二进制.P67二进制求和;

import java.util.Deque;
import java.util.LinkedList;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().addBinary("1010", "1011"));
    }

    public String addBinary(String a, String b) {

        /*
         * 讨论是否需要进位c
         *
         * 结果为 (sum = 0) % 2 = 0
         * 结果为 (sum = 1) % 2 = 1
         * 结果为 (sum = 2) % 2 = 0
         * 结果为 (sum = 3) % 2 = 1
         *
         * c 为进位 0 / 2 = 0 不需要进位 c = 0
         * c 为进位 1 / 2 = 0 不需要进位 c = 0
         * c 为进位 2 / 2 = 1 需要进位   c = 1
         * c 为进位 3 / 2 = 1 需要进位   c = 1
         * sum = c
         *
         * result = 0
         *  */
        int s = 0;
        int n1 = 0;
        int n2 = 0;
        int p1 = a.length() - 1;
        int p2 = b.length() - 1;

        int len = a.length() > b.length() ? a.length() : b.length();
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (p1 < 0) {
                n1 = 0;
            } else {
                n1 = a.charAt(p1--) - '0';
            }
            if (p2 < 0) {
                n2 = 0;
            } else {
                n2 = b.charAt(p2--) - '0';
            }
            s = n1 + n2 + s;
            ans.append(s % 2);
            s = s / 2;
        }
        ans.append(s == 1 ? s : "");
        return ans.reverse().toString();
    }

    public String addBinary0(String a, String b) {

        /*
         *  1 0 1 0
         *
         *  1 1 1
         * */
        int n1 = 0;
        int n2 = 0;
        int s = 0;
        int c = 0;
        int p1 = a.length() - 1;
        int p2 = b.length() - 1;
        int len = a.length() > b.length() ? a.length() : b.length();
        Deque<String> result = new LinkedList<>();
        int digit = 0;
        for (int i = 0; i < len; i++) {
            if (p1 < 0) {
                n1 = 0;
            } else {
                n1 = a.charAt(p1--) - '0';
            }
            if (p2 < 0) {
                n2 = 0;
            } else {
                n2 = b.charAt(p2--) - '0';
            }
            s = n1 + n2 + c;
            if (s == 3) {
                result.addFirst("1");
                c = 1;
            } else if (s == 2) {
                result.addFirst("0");
                c = 1;
            } else if (s == 1) {
                result.addFirst("1");
                c = 0;
            } else if (s == 0) {
                result.addFirst("0");
                c = 0;
            }
        }
        if (c == 1) {
            result.addFirst("1");
        }
        StringBuilder r = new StringBuilder();
        while (!result.isEmpty()) {
            r.append(result.pop());
        }
        return r.toString();
    }
}
