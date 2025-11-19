package com.ssdlh.algorithm.哈希表.P1207独一无二的出现次数;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int integer = map.get(1);
        //System.out.println(integer);
        System.out.println();
    }

    public boolean uniqueOccurrences(int[] arr) {

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            Integer key = arr[i];
            Integer val = map.getOrDefault(key, 0) + 1;
            map.put(key, val);
        }

        HashSet<Integer> set = new HashSet<>();
        for (Integer next : map.values()) {
            if (set.contains(next)) {
                return false;
            }
            set.add(next);
        }
        return true;
    }






    public boolean uniqueOccurrences0(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();
        HashSet<Integer> set = new HashSet<>();

        for (int x : arr) {
            map.putIfAbsent(x, 1);
            map.put(x, map.get(x) + 1);
        }
        for (Map.Entry<Integer, Integer> x : map.entrySet()) {
            if (set.contains(x.getValue())) {
                return false;
            }
            set.add(x.getValue());
        }
        return true;
    }

}
