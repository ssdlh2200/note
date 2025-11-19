package DLeetcode.数据结构与算法.P605;

public class Solution {
    public static void main(String[] args) {
        int[] arr = {1,0,0,0,1};
        System.out.println(new Solution().canPlaceFlowers(arr, 2));
    }


    /*
    *  花坛的字符串为00,01,10,00四种可能
    *
    *   其中00中间可以插入字符串
    *   若插入字符串，则变为010
    *
    * */
    public boolean canPlaceFlowers(int[] flowerbed, int n){
        int canFlowerNum = 0;
        for (int i = 0; i < flowerbed.length - 1; i++) {
            int a = flowerbed[i];
            int b = flowerbed[i + 1];
            if ( (a | b) == 0){
                canFlowerNum++;
            }
        }
        return n <= canFlowerNum;
    }

}
