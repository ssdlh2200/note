package DLeetcode.数据结构与算法.双指针.P41缺失的第一个正数;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().firstMissingPositive2(new int[]{1, 2, 0}));
        System.out.println(new Solution().firstMissingPositive2(new int[]{3,4,-1,1}));
        System.out.println(new Solution().firstMissingPositive2(new int[]{7,8,9,11,12}));
        System.out.println(new Solution().firstMissingPositive2(new int[]{0}));

    }

    public int firstMissingPositive0(int[] nums){
        int n = nums.length, res = nums.length + 1;

        boolean[] numsBoolean = new boolean[n];
        for (int i = 0; i < n; i++) {
            int temp = nums[i];
            if (temp <= n && temp >= 1){
                numsBoolean[temp - 1] = true;
            }
        }
        for (int i = 0; i < n; i++) {
            if (!numsBoolean[i]){
                res = i + 1;
                break;
            }
        }
        return res;
    }


    public int firstMissingPositive1(int[] nums){
        int n = nums.length;
        int res = nums.length + 1;

        /*
        *条件1：temp是否在i+1位置上
        *条件2：temp是否为1-n
        * */

        for (int i = 0; i < n; i++) {
            int temp = nums[i];
            while (temp >= 1 && temp <= n && temp != nums[temp - 1]){
                swap(nums, i, temp-1);
                temp = nums[i];
            }
        }

        for (int i = 0; i < n; i++) {
            if (i+1 != nums[i]){
                res = i+1;
                break;
            }
        }
        return res;
    }
    public void swap(int[] nums, int a, int b){
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }



    public int firstMissingPositive2(int[] nums) {
        int l = 0, r = nums.length;
        while (l < r){
            int temp = nums[l];
            if (temp == l + 1){
                l++;
            } else if ( temp <= l || temp > r || nums[temp - 1] == temp){
                r--;
                swap(nums, l, r);
            } else {
                swap(nums, l, temp - 1);
            }
        }
        return r + 1;
    }


}
