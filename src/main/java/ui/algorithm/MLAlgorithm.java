package ui.algorithm;

import ui.dataset.Example;

import java.util.List;

public interface MLAlgorithm {
    void fit(List<Example> examples);
    void predict(List<Example> examples);
}
