package com.example.assignment_3_fawzia;

public class TreeNode {
    Software software;
    TreeNode left;
    TreeNode right;

    public TreeNode(Software software) {
        this.software = software;
        this.left = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return software.getName() + " " + software.getVersion();
    }
}