package com.example.assignment_3_fawzia;

import java.util.List;

public class BinarySearchTree {
    TreeNode root;

    public BinarySearchTree() {
        this.root = null;
    }

    // Insert a software into the BST
    public void insert(Software software) {
        root = insertRec(root, software);
    }

    private TreeNode insertRec(TreeNode root, Software software) {
        if (root == null) {
            return new TreeNode(software);
        }

        int comparison = software.getKey().compareToIgnoreCase(root.software.getKey());

        if (comparison < 0) {
            root.left = insertRec(root.left, software);
        } else if (comparison > 0) {
            root.right = insertRec(root.right, software);
        } else {
            // Key already exists, update the software
            root.software = software;
        }

        return root;
    }

    // Search for a software by key (name + version)
    public TreeNode search(String key) {
        return searchRec(root, key);
    }

    private TreeNode searchRec(TreeNode root, String key) {
        if (root == null) {
            return null;
        }

        int comparison = key.compareToIgnoreCase(root.software.getKey());

        if (comparison == 0) {
            return root;
        } else if (comparison < 0) {
            return searchRec(root.left, key);
        } else {
            return searchRec(root.right, key);
        }
    }

    // Delete a software from the BST
    public void delete(String key) {
        root = deleteRec(root, key);
    }

    private TreeNode deleteRec(TreeNode root, String key) {
        if (root == null) {
            return null;
        }

        int comparison = key.compareToIgnoreCase(root.software.getKey());

        if (comparison < 0) {
            root.left = deleteRec(root.left, key);
        } else if (comparison > 0) {
            root.right = deleteRec(root.right, key);
        } else {
            // Node to be deleted found

            // Case 1: Node has no children (leaf node)
            if (root.left == null && root.right == null) {
                return null;
            }

            // Case 2: Node has only one child
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            // Case 3: Node has two children
            // Find the inorder successor (smallest in right subtree)
            TreeNode successor = findMin(root.right);
            root.software = successor.software;
            root.right = deleteRec(root.right, successor.software.getKey());
        }

        return root;
    }

    private TreeNode findMin(TreeNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    // Inorder traversal (sorted order)
    public void inorderTraversal(TreeNode root, List<String> result) {
        if (root != null) {
            inorderTraversal(root.left, result);
            result.add(root.software.toString());
            inorderTraversal(root.right, result);
        }
    }

    // Collect all software in the tree
    public void collectAllSoftware(TreeNode root, List<Software> result) {
        if (root != null) {
            collectAllSoftware(root.left, result);
            result.add(root.software);
            collectAllSoftware(root.right, result);
        }
    }

    // Display tree structure visually
    public void displayTree(TreeNode root, String prefix, boolean isTail, StringBuilder sb) {
        if (root != null) {
            sb.append(prefix);
            sb.append(isTail ? "└── " : "├── ");
            sb.append(root.software.getName());
            if (!root.software.getVersion().isEmpty()) {
                sb.append(" v").append(root.software.getVersion());
            }
            sb.append(" [Qty: ").append(root.software.getQuantity()).append("]");
            sb.append("\n");

            if (root.left != null || root.right != null) {
                if (root.right != null) {
                    displayTree(root.right, prefix + (isTail ? "    " : "│   "), false, sb);
                }
                if (root.left != null) {
                    displayTree(root.left, prefix + (isTail ? "    " : "│   "), true, sb);
                }
            }
        }
    }

    // Get tree height
    public int getHeight() {
        return getHeightRec(root);
    }

    private int getHeightRec(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftHeight = getHeightRec(root.left);
        int rightHeight = getHeightRec(root.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Count total nodes
    public int countNodes() {
        return countNodesRec(root);
    }

    private int countNodesRec(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + countNodesRec(root.left) + countNodesRec(root.right);
    }
}