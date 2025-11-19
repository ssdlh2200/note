package DLeetcode.数据结构与算法.P1431;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static void main(String[] args) {

        int[] arr = {2,3,5,1,3};
        int num = 3;

        List<Boolean> booleans = new Solution().kidsWithCandies(arr, 3);

        System.out.println(booleans);
    }

    /*
    * 思路，找到数组中最大的数，然后将糖果全部分配给他，那么此孩子将由最多的糖果
    * 找到数组中最小的数，如果最小的数分配的糖果加上该孩子本身拥有的糖果数仍然还小于最大的糖果数，那么将不能拥有最多的糖果
    *
    * 2,3,5,1,3
    *
    * 3
    *
    * */
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        List<Boolean> iScandies = new ArrayList<>();
        int maxCandies = -1;
        for (int x : candies){
            if (x > maxCandies){
                maxCandies = x;
            }
        }
        for (int i = 0; i < candies.length; i++) {
            int var1 = candies[i];
            if (var1 + extraCandies < maxCandies){
                iScandies.add(i, false);
            } else {
                iScandies.add(i, true);
            }
        }
        return iScandies;
    }
}
