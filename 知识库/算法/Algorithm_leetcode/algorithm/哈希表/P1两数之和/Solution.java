package com.ssdlh.algorithm.哈希表.P1两数之和;

import java.util.HashMap;

public class Solution {

    public int[] twoSum0(int[] nums, int target) {

        for (int i = 0; i < nums.length; i++) {
            int a = nums[i];
            for (int j = i + 1; j < nums.length; j++) {
                int b = nums[j];
                if (a + b == target){
                    return new int[]{i,j};
                }
            }
        }
        return new int[0];
    }

    public int[] twoSum(int[] nums, int target){

        /*
            target = 9 = a + b

            2 11 15 7

            7 -2 -6 2
        */

        HashMap<Integer, Integer> table = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int a = nums[i];
            int b = target - a;

            if (table.containsKey(b)){
                return new int[]{table.get(b), i};
            }

            table.put(a, i);
        }
        return new int[0];
    }



}
