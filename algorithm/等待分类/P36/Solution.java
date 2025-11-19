package com.ssdlh.algorithm.P36;

class Solution {


    public static void main(String[] args) {
        char[][] board = {{'5','5','.','.','7','.','.','.','.'}
                ,{'6','.','.','1','9','5','.','.','.'}
                ,{'.','9','8','.','.','.','.','6','.'}
                ,{'8','.','.','.','6','.','.','.','3'}
                ,{'4','.','.','8','.','3','.','.','1'}
                ,{'7','.','.','.','2','.','.','.','6'}
                ,{'.','6','.','.','.','.','2','8','.'}
                ,{'.','.','.','4','1','9','.','.','5'}
                ,{'.','.','.','.','8','.','.','7','9'}};
        new Solution().isValidSudoku(board);


    }


    public boolean isValidSudoku(char[][] board) {
        boolean[][] row = new boolean[9][9];
        boolean[][] col = new boolean[9][9];
        boolean[][][] littleBoard = new boolean[3][3][9];


        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                char c = board[i][j];
                if (c != '.') {
                    int number = c - 48;
                    boolean rb = row[i][number - 1];
                    boolean cb = col[j][number - 1];
                    boolean lb = littleBoard[i / 3][j / 3][number - 1];
                    if (rb | cb | lb) {
                        return false;
                    }
                    row[i][number - 1] = true;
                    col[j][number - 1] = true;
                    littleBoard[i / 3][j / 3][number - 1] = true;
                }
            }
        }
        return true;
    }
}