package DLeetcode.数据结构与算法.P207;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {

        int numCourse = 2;
        int[][] prerequisites = {{1,0}};
        int[] order = new Solution().findOrder(numCourse, prerequisites);
        System.out.println(Arrays.toString(order));
    }



    /*暴力法*/
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] in = new int[numCourses];
        boolean[] isIn = new boolean[numCourses];
        /*先更新度*/
        for (int i = 0; i < prerequisites.length; i++) {
            in[prerequisites[i][0]] += 1;
        }
        /*找到节点为0的度*/
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in.length; j++) {
                /*判断该节点是否已经剔除*/
                if (in[j] == 0 && !isIn[j]){
                    for (int k = 0; k < prerequisites.length; k++) {
                        if (prerequisites[k][1] == j){
                            in[prerequisites[k][0]] -= 1;
                        }
                    }
                    isIn[j] = true;
                }
            }
        }
        for (int i = 0; i < isIn.length; i++) {
            if (!isIn[i]){
                return false;
            }
        }
        return true;
    }


    public int[] findOrder(int numCourses, int[][] prerequisites) {

        int[] in = new int[numCourses];
        int[] order = new int[numCourses];

        boolean[] isIn = new boolean[numCourses];


        /*先更新度*/
        for (int i = 0; i < prerequisites.length; i++) {
            in[prerequisites[i][0]] += 1;
        }
        /*找到节点为0的度*/
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in.length; j++) {
                /*判断该节点是否已经剔除*/
                if (in[j] == 0 && !isIn[j]){
                    for (int k = 0; k < prerequisites.length; k++) {
                        if (prerequisites[k][1] == j){
                            in[prerequisites[k][0]] -= 1;
                        }
                    }
                    isIn[j] = true;
                    order[i] = j;
                }
            }
        }
        for (int i = 0; i < isIn.length; i++) {
            if (!isIn[i]){
                return new int[]{0};
            }
        }
        return order;

    }



}
