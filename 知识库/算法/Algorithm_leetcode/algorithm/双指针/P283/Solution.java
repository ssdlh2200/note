package DLeetcode.数据结构与算法.双指针.P283;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.moveZeroes2(new int[]{0,1,0,3,12});

    }
    public void moveZeroes0(int[] nums){
        for (int i = 0; i < nums.length; i++) {
            int start = nums[i];
            if (start != 0){
                continue;
            }
            int temp = i;
            while (true) {
                temp++;
                if (temp >= nums.length) {
                    break;
                }
                if (nums[temp] != 0){
                    nums[i] = nums[temp];
                    nums[temp] = start;
                    break;
                }
            }
            if (temp == nums.length){
                break;
            }
        }
        System.out.println(Arrays.toString(nums));
    }

    /*
    *
    * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。

        请注意 ，必须在不复制数组的情况下原地对数组进行操作。
    * */
    public void moveZeroes1(int[] nums){
        /*
        * left:第一个零
        * right：第一个非零
        * */
        int l = 0;
        int r = 0;

        for (int i = 0; i < nums.length; i++) {
            l = nums[i];
            if (l == 0){
                for (int j = i+1; j < nums.length; j++) {
                    r = nums[j];
                    if (r != 0){
                        nums[i] = r;
                        nums[j] = l;
                        break;
                    }
                }
            }
        }
/*
        System.out.println(Arrays.toString(nums));
*/
    }
    public void moveZeroes2(int[] nums){
        int l = 0, r = 0, n = nums.length;
        while ( r < n ){
            int temp = nums[r];
            if (temp != 0){
                nums[r] = nums[l];
                nums[l] = temp;
                l++;
            }
            r++;
        }
        System.out.println(Arrays.toString(nums));
    }

}
