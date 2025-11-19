package com.ssdlh.algorithm.P15;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().threeSum(new int[]{0,1,1}));
        System.out.println(new Solution().threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
    }
    List<List<Integer>> res = new ArrayList<>();
    public List<List<Integer>> threeSum(int[] nums){
        Arrays.sort(nums);

        int l = 0, r = nums.length - 1, mid = 0, n = nums.length;
        while (l+1 < r){
            int temp = nums[l] + nums[r];
            if (temp >= 0){
                mid = l+1;
                while (mid < r && nums[mid] <= 0){
                    if (temp + nums[mid] == 0){
                        addToList(nums[l], nums[r], nums[mid]);
                    }
                    mid++;
                }
                r--;
            } else if (temp < 0){
                mid = r - 1;
                while (mid > l && nums[mid] > 0){
                    if (temp + nums[mid] == 0){
                        addToList(nums[l], nums[r], nums[mid]);
                    }
                    mid--;
                }
                l++;
            }
        }
        return res;
    }
    public void addToList(int l, int r, int mid){
        ArrayList<Integer> ans = new ArrayList<>();
        ans.add(l);
        ans.add(r);
        ans.add(mid);
        res.add(ans);
    }

}
