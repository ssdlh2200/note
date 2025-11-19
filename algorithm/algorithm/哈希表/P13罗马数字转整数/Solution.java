package com.ssdlh.algorithm.哈希表.P13罗马数字转整数;

import java.util.HashMap;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();

        System.out.println("solution.romanToInt(\"IVI\") = " + solution.romanToInt("IVI"));
        System.out.println("solution.romanToInt(\"LVIII\") = " + solution.romanToInt("LVIII"));
    }



    public int romanToInt0(String s) {
        HashMap<Character, Integer> map = new HashMap<>() {{
            put('I', 1);
            put('V', 5);
            put('X', 10);
            put('L', 50);
            put('C', 100);
            put('D', 500);
            put('M', 1000);
        }};
        int result = 0;
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            int l = i;
            int r = i + 1;
            int lValue = map.get(charArray[l]);
            if (r < charArray.length) {
                int rValue = map.get(charArray[r]);
                if (lValue < rValue) {
                    result += rValue - lValue;
                    i = r;
                    continue;
                }
            }
            result += lValue;

        }
        return result;
    }

    public int romanToInt(String s) {

        HashMap<Character, Integer> map = new HashMap<>() {{
            put('I', 1);
            put('V', 5);
            put('X', 10);
            put('L', 50);
            put('C', 100);
            put('D', 500);
            put('M', 1000);
        }};

        int res = 0;
        int preValue = map.get(s.charAt(0));
        for (int i = 1; i < s.length(); i++) {
            int curValue = map.get(s.charAt(i));
            if (preValue < curValue ){
                res -= preValue;
            } else {
                res += preValue;
            }
            preValue = curValue;
        }
        res += preValue;
        return res;
    }
}
