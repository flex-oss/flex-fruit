/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
