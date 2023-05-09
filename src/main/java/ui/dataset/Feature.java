package ui.dataset;

import java.util.Objects;

public record Feature(String feature, String value) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature1 = (Feature) o;
        return Objects.equals(feature, feature1.feature) && Objects.equals(value, feature1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature, value);
    }
}
