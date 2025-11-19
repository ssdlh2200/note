package DLeetcode.数据结构与算法.双指针.P881救生艇;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.numRescueBoats(new int[]{1, 2}, 3));
        System.out.println(solution.numRescueBoats(new int[]{3,2,2,1}, 3));
        System.out.println(solution.numRescueBoats(new int[]{3,2,3,2,2}, 6));
        System.out.println(solution.numRescueBoats(new int[]{3,5,3,4}, 5));
    }

    public int numRescueBoats(int[] people, int limit){
        Arrays.sort(people);
        int l = 0, r = people.length - 1, n = people.length, res = 0;
        while (l <= r){
            int temp = l == r ? people[r] : people[l] + people[r];
            if (temp <= limit){
                l++;
            }
            r--;
            res++;
        }
        return res;
    }
}
