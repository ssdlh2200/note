package DLeetcode.数据结构与算法.滑动窗口.P134加油站;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().canCompleteCircuit(new int[]{1,2,3,4,5}, new int[]{3,4,5,1,2}));
        System.out.println(new Solution().canCompleteCircuit(new int[]{2,3,4}, new int[]{3,4,3}));
        System.out.println(new Solution().canCompleteCircuit(new int[]{2}, new int[]{3}));
        System.out.println(new Solution().canCompleteCircuit(new int[]{3}, new int[]{3}));
        System.out.println(new Solution().canCompleteCircuit(new int[]{3}, new int[]{3}));


    }

    public int canCompleteCircuit(int[] gas, int[] cost) {
        int n = gas.length, l = 0, r = 0, sum = 0, len = 0;
        int[] need = new int[n];

        for (int i = 0; i < n; i++) {
            need[i] = gas[i] - cost[i];
        }


        for (int i = 0; i < n; i++) {

            while (sum >= 0){



            }


        }
        return -1;
    }
}
