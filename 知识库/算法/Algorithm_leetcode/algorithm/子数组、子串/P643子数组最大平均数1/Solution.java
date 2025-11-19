package DLeetcode.数据结构与算法.滑动窗口.P643子数组最大平均数1;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4));
        /*12.75*/
        System.out.println(solution.findMaxAverage(new int[]{5}, 1));
        /*5*/
        System.out.println(solution.findMaxAverage(new int[]{0,0,3,2,4}, 5));
        /*1.8*/
        System.out.println(solution.findMaxAverage(new int[]{0,4,0,3,2}, 1));

        System.out.println(solution.findMaxAverage(new int[]{0,4,4,0,3}, 2));
        /*4*/

    }
    public double findMaxAverage(int[] nums, int k) {
        int n = nums.length, l = 0, r = k - 1;

        int w = 0 ,res = 0;

        for (int i = 0; i <= r; i++) {
            w += nums[i];
        }
        res = w;

        for (int i = r + 1; i < n; i++) {
            w = w - nums[l] + nums[i];
            l++;
            res = Math.max(w, res);
        }

        return 1.0 * res / k;
    }
}
