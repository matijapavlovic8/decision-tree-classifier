package ui.nodes;

import ui.dataset.Feature;

import java.util.List;

public class FeatureNode extends Node {
    private final Feature feature;
    private final List<Node> children;

    public FeatureNode(int depth, Feature feature, List<Node> children) {
        super(depth);
        this.feature = feature;
        this.children = children;

    }

    public Feature getFeature() {
        return feature;
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getDepth() {
        return super.depth;
    }
}
