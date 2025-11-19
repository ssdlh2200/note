package com.ssdlh.algorithm.多项式插值;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Solution {

    static ArrayList<ArrayList<Double>> result = new ArrayList<>(); // 存放符合条件结果的集合
    static ArrayList<Double> path = new ArrayList<>(); // 用来存放符合条件结果

    // 计算线性插值的斜率
    static double getInterpolation(ArrayList<ArrayList<Double>> a, int i) {
        double value = 0;
        value = (a.get(i).get(1) - a.get(i + 1).get(1)) / (a.get(i).get(0) - a.get(i + 1).get(0));
        return value;
    }

    static double getInterpolation(ArrayList<ArrayList<Double>> b, ArrayList<Double> a, int i, int p) {
        double value1 = 0;
        value1 = (a.get(i) - a.get(i + 1)) / (b.get(i).get(0) - b.get(i + p).get(0));
        return value1;
    }

    // 计算组合数
    static void calculateCombinatorialNumber(ArrayList<ArrayList<Double>> a, int k, int startIndex, int endIndex) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = startIndex; i <= endIndex; i++) { // 控制树的横向遍历
            path.add(a.get(i).get(0)); // 处理节点
            calculateCombinatorialNumber(a, k, i + 1, endIndex); // 递归：控制树的纵向遍历，注意下一层搜索要从i+1开始
            path.remove(path.size() - 1); // 回溯，撤销处理的节点
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int n = input.nextInt();
        int m = input.nextInt();

        List<ArrayList<Double>> point = new ArrayList<>();

        List<Double> pointM = new ArrayList<>(m);
        List<Double> midInterpolation1 = new ArrayList<>();
        List<Double> midInterpolation2 = new ArrayList<>();
        List<Double> interpolation = new ArrayList<>();
        List<Double> coefficient = new ArrayList<>(n);

        /*
        *       0   1   2   3   4   5
        *   0
        *   1
        *   2
        *   3
        *   4
        * */

        for (int i = 0; i < n; i++) {
            point.add(i, new ArrayList<>(){{
                add(0, input.nextDouble());
                add(1, input.nextDouble());
            }});
        }
        for (int i = 0; i < m; i++) {
            pointM.add(i, input.nextDouble());
        }



    }




}
