package DLeetcode.数据结构与算法.滑动窗口.P209长度最小的子数组;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}));
        System.out.println(solution.minSubArrayLen(1, new int[]{1}));
        System.out.println(solution.minSubArrayLen(1, new int[]{5}));
        System.out.println(solution.minSubArrayLen(4, new int[]{1, 4, 4}));
        System.out.println(solution.minSubArrayLen(213, new int[]{12, 28, 83, 4, 25, 26, 25, 2, 25, 25, 25, 12}));


    }

    public int minSubArrayLen(int target, int[] nums) {


        int w = 0, l = 0, r = 0, ans = Integer.MAX_VALUE, n = nums.length;

        /*没有滑到最右边时*/
        while (r < n) {
            w = nums[r] + w;
            while (w >= target) {
                ans = Math.min(ans, r - l + 1);
                w = w - nums[l];
                l++;
            }
            r++;
        }
        return ans == Integer.MAX_VALUE ? 0 : ans;
    }
}
/*2023年9月13日16:09:22*/