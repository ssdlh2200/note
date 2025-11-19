package com.ssdlh.algorithm.双指针.P88合并两个有序数组;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        int[] arr1 = new int[]{1, 2, 3, 0, 0, 0};
        int[] arr2 = new int[]{2, 5, 6};
    }

    /*
     * 从前往后合并版本 mergeFrontToLast1
     * */
    public void mergeFrontToLast1(int[] nums1, int m, int[] nums2, int n) {
        /*
         * 时间复杂度o(m+n)
         * 空间复杂度o(m)
         * */
        int[] temp = Arrays.copyOf(nums1, m);

        int i = 0;
        int point1 = 0;
        int point2 = 0;

        while (point1 < m && point2 < n) {
            if (temp[point1] < nums2[point2]) {
                nums1[i++] = temp[point1++];
            } else {
                nums1[i++] = nums2[point2++];
            }
        }
        while (point1 < m) {
            nums1[i++] = temp[point1++];
        }
        while (point2 < n) {
            nums1[i++] = nums2[point2++];
        }
    }

    /*
    * 从后往前合并
    * */
    public void mergeLastToFront(int[] nums1, int m, int[] nums2, int n) {
        int point = m + n - 1;
        m = m - 1;
        n = n - 1;
        while (m >= 0 && n >= 0) {
            if (nums1[m] > nums2[n]) {
                nums1[point--] = nums1[m--];
            } else {
                nums1[point--] = nums2[n--];
            }
        }
        while (n >= 0){
            nums1[point--] = nums2[n--];
        }
    }
}
