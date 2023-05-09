package ui.nodes;

public class Leaf extends Node {
    private final String label;

    public Leaf(int depth, String label) {
        super(depth);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
