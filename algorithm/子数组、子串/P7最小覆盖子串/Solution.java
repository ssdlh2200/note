package DLeetcode.数据结构与算法.滑动窗口.P7最小覆盖子串;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().minWindow1("ACEBDA", "ABD"));
        System.out.println(new Solution().minWindow1("ADOBECODEBANC", "ABC"));
        System.out.println(new Solution().minWindow1("a", "a"));
        System.out.println(new Solution().minWindow1("a", "aa"));
        System.out.println(new Solution().minWindow1("bdab", "ab"));
        System.out.println(new Solution().minWindow1("bba", "ab"));


    }

    /*
    *
    *
    *   BDA
        BANC
        a

        ab
        ba
    * */

    public String minWindow(String s, String t) {
        char[] sCharArray = s.toCharArray();
        char[] tCharArray = t.toCharArray();
        int l = 0, r = 0, n = sCharArray.length, m = tCharArray.length;

        Map<Character, Integer> tMap = new HashMap<>();
        int ansL = 0, ansR = Integer.MAX_VALUE;


        for (char c : tCharArray) {
            tMap.put(c, tMap.getOrDefault(c, 0) + 1);
        }


        while (r < n) {
            char c = sCharArray[r];
            Integer temp = tMap.get(c);

            if (temp != null) {
                tMap.put(c, temp - 1);
                if (temp > 0) {
                    m--;
                }
            }
            if (m == 0) {
                while (true) {
                    char ls = sCharArray[l];
                    if (tMap.containsKey(ls)) {
                        if (tMap.get(ls) + 1 > 0) {
                            break;
                        } else {
                            tMap.put(ls, tMap.get(ls) + 1);
                        }
                    }
                    l++;
                }
                if (r - l <= ansR - ansL) {
                    ansL = l;
                    ansR = r;
                }
            }
            r++;
        }
        return ansR - ansL == Integer.MAX_VALUE ? "" : s.substring(ansL, ansR + 1);
    }


    public String minWindow1(String s, String t) {

        int n = s.length(), m = t.length(), l = 0, r = 0, ansL = 0, ansR = Integer.MAX_VALUE;
        char[] arrS = s.toCharArray(), arrT = t.toCharArray();
        int[] map = new int[256];

        for (char temp : arrT){
            map[temp]++;
        }
        while (r < n){
            char temp = arrS[r];
            if (map[temp] > 0){
                m--;
            }
            /*窗口内的每个字符的需求数量*/
            map[temp]--;
            if (m == 0){
                while (map[arrS[l]] < 0){
                    map[arrS[l]]++;
                    l++;
                }
                if (r - l < ansR - ansL){
                    ansL = l;
                    ansR = r;
                }
            }
            r++;
        }
        return ansR-ansL==Integer.MAX_VALUE ? "" : new String(arrS, ansL, ansR - ansL + 1);
    }
}
