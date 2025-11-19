package DLeetcode.数据结构与算法.P1071;


public class Solution {
    public static void main(String[] args) {
        String s = new Solution().gcdOfStrings("AABAB", "AB");

        System.out.println(s);
    }

    /*
    * 思路:
    *
    * 1.枚举最短的字符串
    *   a.找到最短的字符串
    *   b.从后往前开始每个最短的字符串
    *
    *
    * 2.检查一个字符串是否为多个字符串的公共子串
    *   a.该字符串的长度是否能被父字符串的长度整除
    *   b.
    *       该字符串是否为第一个字符串的子串
    *       该字符串是否为第二个字符串的子串
    *           i.检查该字符串是否为子串的方法
    *               对比第一个，第二个，第三个
    *
    *
    * */
    public String gcdOfStrings(String str1, String str2){

        StringBuilder temp = new StringBuilder(str1.length() < str2.length() ? str1 : str2);

        for (int i = 0; i < temp.length(); i++) {
            String toMatch = temp.substring(0, temp.length() - i);
            int toMatchLen = toMatch.length();
            if (str1.length() % toMatchLen != 0 || str2.length() % toMatchLen != 0){
                continue;
            }
            if (check(str1, toMatch) && check(str2, toMatch)){
                return toMatch;
            }
        }
        return "";
    }

    /*
    * 传进来字符串，检查字符串toMatch是否为字符串str1的约串
    * */
    public boolean check(String str1, String toMatch){
        for (int i = 0; i < str1.length(); i++) {
            char str1Single = str1.charAt(i);
            char toMatchSingle = toMatch.charAt(i % toMatch.length());
            if (str1Single != toMatchSingle){
                break;
            }
            if (i == str1.length() - 1){
                return true;
            }
        }
        return false;
    }

}