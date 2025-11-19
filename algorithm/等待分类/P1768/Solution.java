package DLeetcode.数据结构与算法.P1768;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.mergeAlternately("abc", "pqr"));
        System.out.println(solution.mergeAlternately("abc", "adqw1bd"));

    }

    public String mergeAlternately_1_0(String word1, String word2){
        int length = Math.min(word1.length(), word2.length());
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++){
            buf.append(word1.charAt(i)).append(word2.charAt(i));
        }
        String toAppend = null;
        if (word1.length() > word2.length()){
            toAppend = word1;
        } else {
            toAppend = word2;
        }
        buf.append(toAppend.subSequence(length, toAppend.length()));
        return buf.toString();
    }

    /*
    * 思路，双指针
    * 用两个指针，i和j，分别指向字符串的初始位置
    * 在每次循环中，执行下面两个步骤
    * 如果i没有超过word[i]的范围，将word1[i]加入字符串中
    * 如果j没有超过word[j]的范围，将word2[j]加入字符串中
    *
    * */
    public String mergeAlternately(String word1, String word2){
        int i = 0, j = 0;
        int m = word1.length();
        int n = word2.length();
        StringBuilder buf = new StringBuilder();

        while ( (i + j) != (m + n)){

            if ( i < m ){
                buf.append(word1.charAt(i));
                i++;
            }

            if ( j < n ){
                buf.append(word2.charAt(j));
                j++;
            }
        }
        return buf.toString();
    }

}
