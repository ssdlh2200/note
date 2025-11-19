package DLeetcode.数据结构与算法.滑动窗口.P3无重复字符的最长子串;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().lengthOfLongestSubstring2("abcb"));
        System.out.println(new Solution().lengthOfLongestSubstring2("abcabcbb"));
        System.out.println(new Solution().lengthOfLongestSubstring2("bbbbb"));
        System.out.println(new Solution().lengthOfLongestSubstring2("pwwkew"));
        System.out.println(new Solution().lengthOfLongestSubstring2("abba"));
        System.out.println(new Solution().lengthOfLongestSubstring2("a"));
    }
    public int lengthOfLongestSubstring(String s) {

        HashSet<Character> set = new HashSet<>();
        LinkedList<Character> list = new LinkedList<>();

        char[] charArray = s.toCharArray();
        int n = charArray.length, res = 0;

        for (int i = 0; i < n; i++) {
            char c = charArray[i];
            if (set.contains(c)){
                while (true){
                    if (list.peekFirst() == c){
                        list.removeFirst();
                        break;
                    }
                    set.remove(list.removeFirst());
                }
            }
            set.add(c);
            list.add(c);
            res = Math.max(res, list.size());
        }
        return res;
    }
    public int lengthOfLongestSubstring1(String s) {

        HashSet<Character> set = new HashSet<>();
        char[] charArray = s.toCharArray();

        int res = 0, l = 0, r = 0;

        for (char c : charArray) {
            if (set.contains(c)) {
                while (true){
                    set.remove(charArray[l]);
                    if (charArray[l++] == c){
                        break;
                    }
                }
            }
            set.add(c);
            res = Math.max(res, r - l + 1);
            r++;
        }
        return res;
    }
    public int lengthOfLongestSubstring2(String s) {

        HashMap<Character, Integer> map = new HashMap<>();
        char[] charArray = s.toCharArray();

        int res = 0, l = 0, r = 0, n = charArray.length;

        while (r < n){
            char c = charArray[r];
            if (map.containsKey(c)){
                l = Math.max(l, map.get(c) + 1);
            }
            map.put(c, r);
            res  = Math.max(res, r - l + 1);
            r++;
        }
        return res;
    }
}
