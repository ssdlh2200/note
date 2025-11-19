package DLeetcode.数据结构与算法.P151;

public class Solution {
    public static void main(String[] args) {
        long t0 = System.nanoTime();
        System.out.println(new Solution().reverseWords("hello world"));
        long t1 = System.nanoTime();
        System.out.println(t1 - t0);

    }



    /*
    * 双指针
    * 找到第一个单词首字母的下标记为index1
    * 找到第一个单词为尾字母的下标记为index2
    *
    * 但是要找到单词
    *   hello   world
    *
    *
    *
    * 然后将index2向前移动
    *
    *
    * */
    public String reverseWords(String s){

        char[] charArray = s.toCharArray();
        StringBuilder newStr = new StringBuilder();
        int index1 = -1, index2 = -1;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c != ' ' && index1 == -1){
                index1 = i;
                continue;
            }
            if (c == ' ' && index1 != -1){
                index2 = i;
                for (int j = index1; j < index2 ; j++) {
                    char var1 = charArray[j];
                    newStr.append(var1);
                }
                index2 = -1;
            }
        }
        return newStr.toString();
    }
}
