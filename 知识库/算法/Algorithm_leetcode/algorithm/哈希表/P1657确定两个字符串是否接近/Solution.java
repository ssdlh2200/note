package DLeetcode.数据结构与算法.哈希表.P1657确定两个字符串是否接近;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.closeStrings1("abbzccca", "babzzczc"));
        System.out.println(solution.closeStrings1("abbbzcf", "babzzcz"));
        System.out.println(solution.closeStrings1("abbzzca", "babzzcz"));
        System.out.println(solution.closeStrings1("aaa", "aaa"));

        System.out.println(solution.closeStrings(
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"));



    }

    public boolean closeStrings(String word1, String word2){

        int n1 = word1.length(), n2 = word2.length();
        if (n1 != n2){
            return false;
        }
        Map<Character, Integer> map1 = new HashMap<>();
        Map<Character, Integer> map2 = new HashMap<>();
        HashSet<Character> set1 = new HashSet<>();
        HashSet<Character> set2 = new HashSet<>();

        for (int i = 0; i < n1; i++) {
            char c1 = word1.charAt(i);
            char c2 = word2.charAt(i);
            map1.put(c1, map1.getOrDefault(c1, 0) + 1);
            map2.put(c2, map2.getOrDefault(c2, 0) + 1);
            set1.add(c1);
            set2.add(c2);
        }

        if (map1.size() != map2.size() || set1.size() != set2.size()){
            return false;
        }
        /*检查是否相互包含字母*/
        for (char c : set1){
            if (!set2.remove(c)){
                return false;
            }
        }
        /*检查字母出现的次数相同*/
        Object[] res1 = map1.values().toArray();
        Object[] res2 = map2.values().toArray();
        Arrays.sort(res1);
        Arrays.sort(res2);
        for (int i = 0; i < res1.length; i++) {
            if ((int) (Integer) res1[i] != (Integer) res2[i]){
                return false;
            }
        }
        return true;
    }


    public boolean closeStrings1(String word1, String word2){
        int n1 = word1.length(), n2 = word2.length();
        if (n1 != n2){
            return false;
        }
        char[] charArray1 = word1.toCharArray();
        char[] charArray2 = word2.toCharArray();
        int[] table1 = new int[26];
        int[] table2 = new int[26];
        for (int i = 0; i < n1; i++) {
            table1[charArray1[i] - 'a']++;
            table2[charArray2[i] - 'a']++;
        }
        /*检查是否相互包含字母*/
        for (int i = 0; i < 26; i++) {
            if (table1[i] + table2[i] == 0){
                continue;
            }
            if (table1[i] == 0 || table2[i] == 0){
                return false;
            }
        }
        /*检查字母出现的次数相同*/
        Arrays.sort(table1);
        Arrays.sort(table2);
        for (int i = 0; i < 26; i++) {
            if (table1[i] != table2[i]){
                return false;
            }
        }
        return true;
    }
}
