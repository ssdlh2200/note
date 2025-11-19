package DLeetcode.数据结构与算法.哈希表.P2352相等行列对;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.equalPairs(new int[][]{{3, 2, 1}, {1, 7, 6}, {2, 7, 7}}));


    }
    /*暴力法*/
    public int equalPairs(int[][] grid) {
        int n = grid.length, ans = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                /*遍历n*n次*/
                /*第0行*/
                for (int k = 0; k < n; k++) {
                    int row = grid[i][k];
                    int col = grid[k][j];
                    if (row != col){
                        break;
                    }
                    if (k == n - 1){
                        ans++;
                    }
                }
            }
        }
        return ans;
    }
}
