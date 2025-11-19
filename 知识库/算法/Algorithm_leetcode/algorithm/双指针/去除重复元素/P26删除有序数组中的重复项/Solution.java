package com.ssdlh.algorithm.双指针.去除重复元素.P26删除有序数组中的重复项;

public class Solution {
    public int removeDuplicates(int[] nums){
        int left = 0;
        int prev = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            if (prev != nums[right]){
                nums[left++] = nums[right];
                prev = nums[right];
            }

        }
        return left;
    }
}
