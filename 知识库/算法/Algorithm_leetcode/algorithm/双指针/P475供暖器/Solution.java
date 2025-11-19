package DLeetcode.数据结构与算法.双指针.P475供暖器;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().findRadius(new int[]{1, 2, 3}, new int[]{2}));
        System.out.println(new Solution().findRadius(new int[]{1,2,3,4}, new int[]{1,4}));
        System.out.println(new Solution().findRadius(new int[]{1,5}, new int[]{2}));
        System.out.println(new Solution().findRadius(new int[]{1,5}, new int[]{1,5}));
    }


    public int findRadius(int[] houses, int[] heaters){
        Arrays.sort(houses);
        Arrays.sort(heaters);

        int l = 0, r = 0, n = houses.length, m = heaters.length, res = 0;

        while (l < n){
            int s = Math.abs(heaters[r] - houses[l]);
            while (r + 1 < m){
                int temp = Math.abs(heaters[r + 1] - houses[l]);
                if (temp > s){
                    break;
                }
                s = temp;
                r++;
            }
            res = Math.max(s, res);
            l++;
        }
        return res;
    }
}
