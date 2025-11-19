package DLeetcode.数据结构与算法.递归.寻找数组最大值;


/*
*
*
* */
public class Solution {
    public static void main(String[] args) {
        int[] arr = new int[]{4,5,3,6,1,8,5};


        Solution solution = new Solution();
        System.out.println(solution.f1(arr, 0, arr.length - 1));
    }

    public int f(int[] arr, int l, int r){
        if (l == r){
            return arr[l];
        }
        int mid = (l + r) / 2;
        int lmax = f(arr, l, mid);
        int rmax = f(arr, mid + 1, r);
        return Math.max(lmax, rmax);
    }

    public int f1(int[] arr, int l, int r){
        if (l == r){
            return arr[l];
        }
        int mid = (l + r) / 2;
        return Math.max(f(arr, l, mid), f(arr, mid + 1, r));
    }
}
