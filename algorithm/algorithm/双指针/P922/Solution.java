package DLeetcode.数据结构与算法.双指针.P922;

public class Solution {
    public static void main(String[] args) {

    }
    /*
    *
    *
    * 使用额外数组
    * */
    public int[] sortArrayByParityII0(int[] nums) {
        int[] res = new int[nums.length];
        int l = 0;
        int r = 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] % 2 == 0){
                res[l] = nums[i];
                l+=2;
            } else {
                res[r] = nums[i];
                r+=2;
            }
        }
        return res;
    }

    /*
    *
    * 不使用额外数组
    * */
    public int[] sortArrayByParityII(int[] nums){
        int n = nums.length, even = 0, odd = 1;
        while (even < n && odd < n){
            int temp = nums[n-1];
            if ((temp & 1) == 1){
                nums[n-1] = nums[odd];
                nums[odd] = temp;
                odd+=2;
            } else {
                nums[n-1] = nums[even];
                nums[even] = temp;
                even+=2;
            }
        }
        return nums;
    }

}
