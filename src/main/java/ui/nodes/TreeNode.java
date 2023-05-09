package ui.nodes;

import java.util.List;

public class TreeNode extends Node{
    private List<Node> children;
    private String feature;
    public TreeNode(int depth, String feature, List<Node> children) {
        super(depth);
        this.feature = feature;
        this.children = children;
    }

    public List<Node> getChildren() {
        return children;
    }

    public String getFeature() {
        return feature;
    }
}
