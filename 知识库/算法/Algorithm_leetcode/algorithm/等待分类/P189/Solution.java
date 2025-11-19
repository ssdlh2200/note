package DLeetcode.数据结构与算法.P189;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        new Solution().rotate2(new int[]{1,2,3,4,5,6,7}, 3);
    }

    public void rotate0(int[] nums, int k) {
        k = k % nums.length;
        int point = nums.length - k;
        int[] newNums = new int[nums.length];
        int index = 0;
        for (int i = point; i < nums.length; i++) {
            newNums[index++] = nums[i];
        }
        for (int i = 0; i < point; i++) {
            newNums[index++] = nums[i];
        }
        System.arraycopy(newNums, 0, nums, 0, nums.length);
    }

    public void rotate1(int[] nums, int k) {
        int n = nums.length;
        k = k % nums.length;
        for (int j = 0; j < k; j++) {
            /*把数组整体向后偏移一位*/
            int last = nums[n - 1];
            for (int i = n - 1; i >= 1; i--) {
                nums[i] = nums[i - 1];
            }
            nums[0] = last;
            System.out.println(Arrays.toString(nums));
        }
    }
    public void rotate2(int[] nums, int k){
        k = k % nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }
    public void reverse(int[] nums, int start, int end){
        while (start < end){
            int temp = nums[end];
            nums[end] = nums[start];
            nums[start] = temp;
            start++;
            end--;
        }
    }
}
