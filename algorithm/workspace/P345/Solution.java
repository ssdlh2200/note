package DLeetcode.数据结构与算法.P345;

import java.util.LinkedList;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().reverseVowels("aA"));


    }

    /*
    *  元音字母 a e i o u
    * hello
    * eo ---> oe
    * holle
    *
    * leetcode
    * eeoe ---> eoee
    * leotcede
    *
    * 找到字符串中的元音字符
    *   1.将字符串转换为字符数组存储
    *   2.创建布尔数组对应字符数组存储是否为元音字母
    *   3.将找到的元音字符存储下来
    * 将元音字符翻转
    *
    *
    *
    * */
    public String reverseVowels(String s){

        char[] vowelsArray = s.toCharArray();
        boolean[] isVowels = new boolean[s.length()];
        char[] vowels = {'a', 'A', 'e', 'E', 'i', 'I', 'o', 'O', 'u', 'U'};
        LinkedList<Character> vowelsList = new LinkedList<>();

        for (int i = 0; i < vowelsArray.length; i++) {
            char c = vowelsArray[i];
            boolean result = containsVowel(c, vowels);
            if (result){
                isVowels[i] = true;
                vowelsList.push(c);
            }
        }
        for (int i = 0; i < vowelsArray.length; i++) {
            if (isVowels[i]){
                vowelsArray[i] = vowelsList.pop();
            }
        }

        return new String(vowelsArray);
    }
    public boolean containsVowel(char c, char[] vowels){
        for (char vowel : vowels) {
            if (c == vowel) {
                return true;
            }
        }
        return false;
    }



}
