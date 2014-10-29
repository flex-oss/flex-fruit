package org.cdlflex.fruit;

import java.io.Serializable;
import java.util.Objects;

/**
 * A specific sort specification which is a tuple of a sort key and a sort order.
 */
public class SortSpecification implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String key;
    private final SortOrder sortOrder;

    public SortSpecification(String key) {
        this(key, SortOrder.ASC);
    }

    public SortSpecification(String key, SortOrder sortOrder) {
        this.key = key;
        this.sortOrder = sortOrder;
    }

    public String getKey() {
        return key;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SortSpecification that = (SortSpecification) o;
        return (Objects.equals(key, that.key)) && (Objects.equals(sortOrder, that.sortOrder));
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, sortOrder);
    }
}
