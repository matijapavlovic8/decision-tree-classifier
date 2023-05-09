package ui.dataset;

import java.util.List;
import java.util.Objects;

public record Example (List<Feature> features, String label) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Example example = (Example) o;
        return Objects.equals(features, example.features) && Objects.equals(label, example.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features, label);
    }

    public String getFeatureValue(String feature) {
        return features.stream().filter(f -> f.feature().equals(feature)).map(Feature::value).findFirst().orElse(null);
    }



}
