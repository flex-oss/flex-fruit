package org.cdlflex.fruit;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * OrderBy clause used for repositories and page sorting.
 */
public class OrderBy implements Serializable {

    private static final long serialVersionUID = 1L;

    private Collection<SortSpecification> sort;

    public OrderBy() {
        this(new LinkedHashSet<SortSpecification>());
    }

    public OrderBy(String property) {
        this(new SortSpecification(property));
    }

    public OrderBy(String property, SortOrder order) {
        this(new SortSpecification(property, order));
    }

    public OrderBy(SortSpecification sortSpecification) {
        this();
        by(sortSpecification);
    }

    public OrderBy(Collection<SortSpecification> sort) {
        this.sort = sort;
    }

    /**
     * Adds the given sort specification.
     *
     * @param sortSpecification the sort specification
     * @return this for chaining
     */
    public OrderBy by(SortSpecification sortSpecification) {
        sort.add(sortSpecification);
        return this;
    }

    public Collection<SortSpecification> getSort() {
        return sort;
    }

}
