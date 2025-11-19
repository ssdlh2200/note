package DLeetcode.数据结构与算法.双指针.P43接雨水;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println(new Solution().trap(new int[]{4,2,0,3,2,5}));
    }

    /*双指针做法
    *
    * 先求0-i位置上的最大值和n-i位置上的最大值
    *
    * */
    public int trap(int[] height){
        int n = height.length, res = 0;
        int[] maxLeft = new int[n];
        int[] maxRight = new int[n];

        maxLeft[0] = height[0];
        for (int i = 1; i < n - 1; i++) {
            maxLeft[i] = Math.max(height[i], maxLeft[i-1]);
        }

        maxRight[n-1] = height[n-1];
        for (int i = n-2; i >= 0; i--) {
            maxRight[i] = Math.max(height[i], maxRight[i+1]);
        }

        for (int i = 0; i < n; i++) {
            res += Math.max(Math.min(maxRight[i], maxLeft[i]) - height[i], 0);
        }
        return res;
    }
}
