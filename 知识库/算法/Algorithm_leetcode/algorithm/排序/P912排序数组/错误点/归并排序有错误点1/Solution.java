package DLeetcode.数据结构与算法.排序.P912排序数组.错误点.归并排序有错误点1;

public class Solution {
    public static void main(String[] args) {

        /*

        错误代码
        System.arraycopy(temp, l0, nums, l0, r1 - l0);
        正确代码
        System.arraycopy(temp, l0, nums, l0, r1 - l0 + 1);

        * */

        int[] nums = new int[]{5,2};
        new Solution().sortArray(nums);
    }

    int[] temp;
    public int[] sortArray(int[] nums) {
        temp = new int[nums.length];
        mergeSort(nums, 0, nums.length - 1);
        return nums;
    }

    public void mergeSort(int[] nums, int l, int r){
        if(l >= r){
            return;
        }
        int mid = (l+r) >> 1;
        mergeSort(nums, l, mid);
        mergeSort(nums, mid+1, r);
        mergeArr(nums, l, mid, mid + 1, r);
    }
    public void mergeArr(int[] nums, int l0, int r0, int l1, int r1){
        int f = l0, s = l1;
        int point = l0;
        while(f <= r0 && s <= r1){
            if(nums[f] <= nums[s]){
                temp[point++] = nums[f++];
            } else {
                temp[point++] = nums[s++];
            }
        }
        while(f <= r0){
            temp[point++] = nums[f++];
        }
        while(s <= r1){
            temp[point++] = nums[s++];
        }
        System.arraycopy(temp, l0, nums, l0, r1 - l0 + 1);
    }


}
