package DLeetcode.数据结构与算法.双指针.P392;

public class Solution {
    public static void main(String[] args) {
        boolean subsequence = new Solution().isSubsequence("axc", "ahbgdc");
        System.out.println(subsequence);
    }

    /*
    *
    * 给定字符串 s 和 t ，判断 s 是否为 t 的子序列。

    字符串的一个子序列是原始字符串删除一些（也可以不删除）字符而不改变剩余字符相对位置形成的新字符串。（例如，"ace"是"abcde"的一个子序列，而"aec"不是）。

    进阶：

    如果有大量输入的 S，称作 S1, S2, ... , Sk 其中 k >= 10亿，你需要依次检查它们是否为 T 的子序列。在这种情况下，你会怎样改变代码？

    致谢：
    * */
    public boolean isSubsequence(String s, String t){

        if (s.equals("")){
            return true;
        }
        int first = 0, second = 0, n = t.length(), m = s.length();
        while ( second < n ){
            if (s.charAt(first) == t.charAt(second)){
                first++;
                if (first == m){
                    return true;
                }
            }
            second++;
        }
        return false;
    }

}
