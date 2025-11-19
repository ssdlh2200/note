package com.ssdlh.algorithm.二叉树.P958二叉树完全性检验;

import com.ssdlh.algorithm.二叉树.A节点.TreeNode;


import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class Solution {


    public boolean isCompleteTree(TreeNode tree) {

        if (tree == null){
            return false;
        }
        List<Integer> ans = new ArrayList<>();

        Deque<TreeNode> queue = new LinkedList<>();
        queue.push(tree);

        while (!queue.isEmpty()){
            int cnt = queue.size();
            while (cnt > 0){
                //拿出当前节点，并将左右子节点入队
                TreeNode treeNode = queue.pollFirst();
                if (treeNode==null){
                    ans.add(null);
                } else {
                    queue.push(treeNode.left);
                    queue.push(treeNode.right);
                }
                cnt--;
            }

        }
        return true;
    }

}
