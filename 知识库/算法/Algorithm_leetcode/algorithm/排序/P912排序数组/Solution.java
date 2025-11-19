package DLeetcode.数据结构与算法.排序.P912排序数组;

public class Solution {
    public static void main(String[] args) {
        int[] arr = {4, 5, 1, 5, 7, 1, 8, 9};
        Solution solution = new Solution();
        solution.sortArray1(arr);
    }
    public int[] sortArray0(int[] nums){
        bubbleSort(nums);
        return nums;
    }

    /*冒泡*/
    public void bubbleSort(int[] nums){
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[j] < nums[i]){
                    swap(nums, i, j);
                }
            }
        }
    }
    public static void swap(int[] nums, int index1, int index2){
        int temp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = temp;
    }


    /*归并排序
    * */

    public int[] sortArray1(int[] nums){
        temp = new int[nums.length];
        mergeSort(nums, 0, nums.length - 1);
        return nums;
    }
    int[] temp;
    public void mergeSort(int[] nums, int l, int r){
        if (l >= r){
            return;
        }
        int mid = (l + r) >> 1;
        mergeSort(nums, l, mid);
        mergeSort(nums, mid + 1, r);
        merge(nums, l, mid, mid + 1, r);
    }
    public void merge(int[] nums, int l0, int r0, int l1, int r1){
        int point = l0, s = l0, e = r1;

        while (l0 <= r0 && l1 <= r1){
            if (nums[l0] <= nums[l1]){
                temp[point++] = nums[l0++];
            } else{
                temp[point++] = nums[l1++];
            }
        }
        while (l0 <= r0){
            temp[point++] = nums[l0++];
        }
        while (l1 <= r1){
            temp[point++] = nums[l1++];
        }
        for (int i = s; i <= e; i++) {
            nums[i] = temp[i];
        }
    }



}
