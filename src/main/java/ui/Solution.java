package ui;

import ui.algorithm.ID3;
import ui.dataset.Example;
import ui.utils.CSVParser;

import java.util.List;

public class Solution {
    public static void main(String[] args) {
        if(args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException("Wrong number of arguments!");
        }

        String trainSetPath = args[0];
        String testSetPath = args[1];
        ID3 model;

        if (args.length == 2) {
            model = new ID3();
        } else {
            int depthHyperparameter = Integer.parseInt(args[2]);
            model = new ID3(depthHyperparameter);
        }

        List<Example> examples = CSVParser.parse(trainSetPath);
        List<Example> predict = CSVParser.parse(testSetPath);


        model.fit(examples);
        model.predict(predict);

    }
}