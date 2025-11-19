package DLeetcode.数据结构与算法.哈希表.P2215找出两数组的不同;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.findDifference(new int[]{1,2,3}, new int[]{2,4,6}));
        System.out.println(solution.findDifference(new int[]{1,2,3,3}, new int[]{2,2,1,1}));
        System.out.println(solution.findDifference(new int[]{1,2,3}, new int[]{2,4,6}));
    }



    public List<List<Integer>> findDifference(int[] nums1, int[] nums2){
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        for (int i : nums1) {
            set1.add(i);
        }
        for (int i : nums2) {
            set2.add(i);
        }
        for (int i : nums2) {
            set1.remove(i);
        }
        for (int i : nums1) {
            set2.remove(i);
        }
        return Arrays.asList(new ArrayList<>(set1), new ArrayList<>(set2));
    }
}
