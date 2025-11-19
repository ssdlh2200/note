package DLeetcode.数据结构与算法.双指针.P1679;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.maxOperations(new int[]{1, 2, 3, 4}, 5));
        System.out.println(solution.maxOperations(new int[]{3, 1, 3, 4, 3}, 6));
        System.out.println(solution.maxOperations(new int[]{1, 2, 3, 4}, 5));

    }
    public int maxOperations0(int[] nums, int k){
        int res = 0, n = nums.length;
        boolean[] isTake = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!isTake[i]){
                for (int j = i + 1; j < n; j++) {
                    if (!isTake[j]){
                        if (nums[i] + nums[j] == k){
                            isTake[i] = true;
                            isTake[j] = true;
                            res++;
                            break;
                        }
                    }
                }
            }
        }
        return res;
    }

    /*
    *
    * 给你一个整数数组 nums 和一个整数 k 。

        每一步操作中，你需要从数组中选出和为 k 的两个整数，并将它们移出数组。

        返回你可以对数组执行的最大操作数。
    * */
    public int maxOperations(int[] nums, int k){
        Arrays.sort(nums);
        int l = 0, r = nums.length - 1, res = 0;
        while (l < r){
            if (nums[r] > k){
                r--;
                continue;
            }
            int temp = nums[r] + nums[l];
            if (temp == k){
                res++;
                r--;
                l++;
            } else if (temp > k){
                r--;
            } else {
                l++;
            }
        }
        return res;
    }
}
