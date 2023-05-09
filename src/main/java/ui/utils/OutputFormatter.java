package ui.utils;

import ui.nodes.FeatureNode;
import ui.nodes.Leaf;
import ui.nodes.Node;
import ui.nodes.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class OutputFormatter {

//    public static void printNode(Node node) {
//        if(node instanceof Leaf leaf) {
//            System.out.println(leaf.getLabel());
//        } else if (node instanceof FeatureNode featureNode) {
//            System.out.print(featureNode.getDepth() + 1 + ":"
//                    + featureNode.getFeature().feature() + "=" +
//                    featureNode.getFeature().value() + " ");
//        }
//    }
//
//    public static void printBranches(Node root) {
//        if (root instanceof Leaf leaf) {
//            printNode(leaf);
//        } else if(root instanceof FeatureNode node) {
//            for (Node child: node.getChildren()) {
//                printNode(node);
//                printBranches(child);
//            }
//        } else if(root instanceof TreeNode treeNode) {
//            for(Node child: treeNode.getChildren()) {
//                printBranches(child);
//            }
//        }
//    }

    public static void printNode(Node node, List<String> path) {
        if (node instanceof Leaf leaf) {
            System.out.println(String.join(" ", path) + " " + leaf.getLabel());
        } else if (node instanceof FeatureNode featureNode) {
            path.add((featureNode.getDepth() + 1) + ":" +
                    featureNode.getFeature().feature() + "=" +
                    featureNode.getFeature().value());

            for (Node child : featureNode.getChildren()) {
                printNode(child, path);
            }
            path.remove(path.size() - 1);
        } else if (node instanceof TreeNode treeNode) {
            for (Node child : treeNode.getChildren()) {
                printNode(child, path);
            }
        }
    }

    public static void printBranches(Node root) {
        System.out.println("[BRANCHES]:");
        List<String> path = new ArrayList<>();
        printNode(root, path);
    }






    public static void printPredictions(List<String> predictions) {
        StringBuilder sb = new StringBuilder();
        sb.append("[PREDICTIONS]: ");
        predictions.forEach(p -> sb.append(p).append(" "));

        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        System.out.print(sb);
    }

    public static void printAccuracy(double accuracy) {
        System.out.printf("[ACCURACY]: %.5f\n", accuracy);
    }

    public static void printConfusionMatrix(int[][] matrix) {
        System.out.println("[CONFUSION_MATRIX]:");
        int dimension = matrix.length;

        for (int[] ints : matrix) {
            for (int j = 0; j < dimension; j++) {
                System.out.print(ints[j]);
                if (j != dimension - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}
