package ui.algorithm;

import ui.dataset.Example;
import ui.dataset.Feature;
import ui.nodes.FeatureNode;
import ui.nodes.Leaf;
import ui.nodes.Node;
import ui.nodes.TreeNode;
import ui.utils.OutputFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ID3 implements MLAlgorithm {
    private final Integer depthHyperParameter;
    private Node root;

    public ID3 (int depthHyperParameter) {
        this.depthHyperParameter = depthHyperParameter;
    }

    public ID3() {
        this.depthHyperParameter = null;
    }

    @Override
    public void fit(List<Example> examples) {
        root = id3(examples, examples, getFeatureSet(examples), null, 0);
        OutputFormatter.printBranches(root);
    }

    @Override
    public void predict(List<Example> examples) {
        Node node = Objects.requireNonNull(root, "Can't predict nothing if model isn't trained!");
        List<String> predictions = new ArrayList<>();
        for (Example example : examples) {
            String prediction = getPrediction(example, node);
            predictions.add(prediction != null ? prediction : getMostCommonExampleLabel(examples));
        }
        OutputFormatter.printPredictions(predictions);
        OutputFormatter.printAccuracy(getAccuracy(examples, predictions));
        OutputFormatter.printConfusionMatrix(createConfusionMatrix(examples, predictions));
    }

    private String getPrediction(Example example, Node node) {
        if (node instanceof Leaf) {
            return ((Leaf) node).getLabel();
        } else if (node instanceof FeatureNode featureNode) {
            String feature = featureNode.getFeature().feature();
            for (Node child : featureNode.getChildren()) {
                if (example.getFeatureValue(feature).equals(featureNode.getFeature().value())) {
                    return getPrediction(example, child);
                }
            }
        } else if (node instanceof TreeNode treeNode) {
            for (Node child : treeNode.getChildren()) {
                if (getPrediction(example, child) != null) {
                    return getPrediction(example, child);
                }
            }
        }
        return null;
    }

    private double getAccuracy(List<Example> examples, List<String> predictions) {
        int count = 0;
        for (int i = 0; i < examples.size(); i++) {
            if (examples.get(i).label().equals(predictions.get(i))) {
                count++;
            }
        }
        return (double) count / examples.size();
    }

    private Node id3(List<Example> D, List<Example> Dparent, Set<String> X, String y, int depth) {
        if (D.isEmpty()) {
            return new Leaf(depth, getMostCommonExampleLabel(Dparent));
        }

        String mcl = getMostCommonExampleLabel(D);
        if (X.isEmpty() || (depthHyperParameter != null && depth == depthHyperParameter) || (D.equals(getExamplesWithLabel(D, mcl)))) {
            return new Leaf(depth, mcl);
        }

        String mdf = getMostDiscriminativeFeature(D, X);
        List<Node> subtrees = new ArrayList<>();

        for (String value: getAllFeatureValues(D, mdf)) {
            Feature newFeature = new Feature(mdf, value);
            List<Example> newExamples = getExamplesWithFeature(D, newFeature);
            Set<String> newX = new HashSet<>(X);
            newX.remove(mdf);
            Node node = id3(newExamples, D, newX, mcl, depth + 1);
            subtrees.add(new FeatureNode(depth, newFeature, List.of(node)));
        }
        return new TreeNode(depth, mdf, subtrees);
    }

    private String getMostCommonExampleLabel(List<Example> examples) {
        Map<String, Integer> labelOccurence = new HashMap<>();
        for (Example e: examples) {
            int value = labelOccurence.getOrDefault(e.label(), 0);
            labelOccurence.put(e.label(), value + 1);
        }
        String mcl = null;
        Integer maxOcc = 0;
        for (Map.Entry<String, Integer> entry: labelOccurence.entrySet()) {
            if (entry.getValue() > maxOcc) {
                mcl = entry.getKey();
                maxOcc = entry.getValue();
            } else if (entry.getValue().equals(maxOcc) && (mcl != null && entry.getKey().compareTo(mcl) < 0)) {
                mcl = entry.getKey();
            }
        }
        return mcl;
    }

    private String getMostDiscriminativeFeature(List<Example> examples, Set<String> featureLabels) {
        String mdf = null;
        double maxInformationGain = Double.MIN_VALUE;

        for(String fl: featureLabels) {
            double informationGain = getInformationGain(examples, fl);
            if (informationGain > maxInformationGain) {
                maxInformationGain = informationGain;
                mdf = fl;
            }
        }
        System.out.println();
        return mdf;
    }

    private Set<String> getAllFeatureValues(List<Example> examples, String feature) {
        return examples.stream()
                .flatMap(e -> e.features().stream()
                        .filter(f -> f.feature().equals(feature))
                        .map(Feature::value))
                .collect(Collectors.toSet());
    }

    private List<Example> getExamplesWithFeature(List<Example> examples, Feature feature) {
        return examples.stream()
                .filter(e -> e.features().stream().anyMatch(f -> f.equals(feature)))
                .collect(Collectors.toList());
    }

    private double getInformationGain(List<Example> examples, String feature) {
        double entrySetEntropy = getEntropy(examples);
        Set<String> featureValues = getAllFeatureValues(examples, feature);
        Set<Feature> features = new HashSet<>();
        for (String value: featureValues) {
            features.add(new Feature(feature, value));
        }

        double totalEntropy = 0.0;
        for (Feature f: features) {
            List<Example> examplesWithFeature = getExamplesWithFeature(examples, f);
            double weight = (double) examplesWithFeature.size() / examples.size();
            totalEntropy += getEntropy(examplesWithFeature) * weight;
        }

        System.out.printf(Locale.US, "IG(%s)=%.4f ", feature, (entrySetEntropy - totalEntropy));
        return entrySetEntropy - totalEntropy;

    }

    private List<Example> getExamplesWithLabel(List<Example> examples, String label) {
        return examples.stream().filter(e -> e.label().equals(label)).collect(Collectors.toList());
    }

    private double getEntropy(List<Example> examples) {
        Map<String, Integer> labelOccurence = new HashMap<>();
        for(Example e: examples) {
            Integer count = labelOccurence.getOrDefault(e.label(), 0) + 1;
            labelOccurence.put(e.label(), count);
        }
        double entropy = 0;
        for (Map.Entry<String, Integer> entry: labelOccurence.entrySet()) {
            double weight = (double) entry.getValue() / examples.size();
            entropy -= weight * (Math.log(weight) / Math.log(2));
        }
        return entropy;
    }

    private Set<String> getFeatureSet(List<Example> examples) {
        return examples.get(0)
                .features()
                .stream()
                .map(Feature::feature)
                .collect(Collectors.toSet());
    }

    private Set<String> getAllLabels(List<Example> examples) {
        return examples.stream().map(Example::label).collect(Collectors.toSet());
    }

    private int[][] createConfusionMatrix(List<Example> examples, List<String> predictions) {
        List<String> labels = getAllLabels(examples).stream().sorted().toList();
        int dimension = labels.size();
        int[][] matrix = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            Arrays.fill(matrix[i], 0);
        }
        
        for (int i = 0; i < examples.size(); i++) {
            String trueLabel = examples.get(i).label();
            String predictedLabel = predictions.get(i);
            matrix[labels.indexOf(trueLabel)][labels.indexOf(predictedLabel)]++;

        }
        return matrix;

    }

}
