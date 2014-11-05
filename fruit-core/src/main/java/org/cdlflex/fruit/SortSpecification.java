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
