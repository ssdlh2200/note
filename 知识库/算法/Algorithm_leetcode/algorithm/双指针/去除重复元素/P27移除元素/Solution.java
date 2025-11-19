package com.ssdlh.algorithm.双指针.去除重复元素.P27移除元素;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().removeElement(new int[]{0, 1, 2, 2, 3, 0, 4, 2}, 2));
    }

    public int removeElement(int[] nums, int val) {
        int left = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != val){
                nums[left++] = nums[right];

            }
        }
        return nums.length - left;
    }

    public int removeElement0(int[] nums, int val) {
        int tail = nums.length - 1;
        int result = 0;
        for (int i = 0; i < tail; i++) {
            if (nums[i] == val){
                result++;
                while (nums[tail] == val && tail > i){
                    tail--;
                    result++;
                }
                int temp = nums[i];
                nums[i] = nums[tail];
                nums[tail--] = temp;
            }
        }
        return nums.length - result;
    }
}
