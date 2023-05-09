package ui.utils;

import ui.dataset.Example;
import ui.dataset.Feature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    public static List<Example> parse(String path) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));
            String[] headers = lines.get(0).split(",");
            lines.remove(0);
            int len = headers.length;
            List<Example> examples = new ArrayList<>();
            for (String line: lines) {
                List<Feature> features = new ArrayList<>();
                String[] featureValues = line.split(",");
                for (int i = 0; i < len - 1; i++) {
                    Feature f = new Feature(headers[i], featureValues[i]);
                    features.add(f);
                }
                examples.add(new Example(features, featureValues[len - 1]));
            }
            return examples;

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
