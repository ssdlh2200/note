package DLeetcode.数据结构与算法.链表.P58最后一个单词的长度;

public class Solution {
    public static void main(String[] args) {



    }
    public int lengthOfLastWord(String s) {

        int n = s.length();
        char[] arr = s.toCharArray();

        int l = n-1, r = n-1;

        while(arr[r] == ' '){
            r--;
        }
        l = r;
        while(l >=0 && arr[l] != ' '){
            l--;
        }
        return l<0 ? r+1 : r-l;
    }

}
