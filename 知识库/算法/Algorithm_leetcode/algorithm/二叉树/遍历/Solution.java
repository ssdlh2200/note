package com.ssdlh.algorithm.二叉树.遍历;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    public static void main(String[] args) {

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        Solution solution = new Solution();
        solution.preorderTraversal(root);

    }


    /*前序遍历递归法*/
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        preorder(root, ans);
        return ans;
    }
    public void preorder(TreeNode node, List<Integer> ans){
        if (node == null){
            return;
        }
        ans.add(node.val);
        preorder(node.left, ans);
        preorder(node.right, ans);
    }

    /*前序遍历迭代法*/
    public List<Integer> preorderTraversal1(TreeNode root){

        ArrayList<Integer> ans = new ArrayList<>();






        return null;
    }





}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
