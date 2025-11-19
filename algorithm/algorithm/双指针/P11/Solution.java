package DLeetcode.数据结构与算法.双指针.P11;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().maxArea(new int[]{1,8,6,2,5,4,8,3,7}));

        /*
        *  1,3,2,5,25,24,5
        *  1,8,6,2,5,4,8,3,7
        * */
    }

    /*
    *
    *
    *
    * 给定一个长度为 n 的整数数组 height 。有 n 条垂线，第 i 条线的两个端点是 (i, 0) 和 (i, height[i]) 。

    找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。

    返回容器可以储存的最大水量。

    说明：你不能倾斜容器。
    * */
    public int maxArea(int[] height){
        int l = 0, r = height.length-1, size = 0;
        while (l < r){
            size  = Math.max((r - l) * Math.min(height[r], height[l]), size);
            if (height[l] < height[r]){
                l++;
            } else {
                r--;
            }
        }
        return size;
    }
}
