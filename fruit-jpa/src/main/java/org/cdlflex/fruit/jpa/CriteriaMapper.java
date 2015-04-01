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
package org.cdlflex.fruit.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.cdlflex.fruit.Connective;
import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Operator;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.Range;
import org.cdlflex.fruit.SortOrder;
import org.cdlflex.fruit.SortSpecification;

/**
 * Maps the {@code org.cdlflex.fruit} data model to {@code javax.persistence.criteria} classes.
 */
public class CriteriaMapper {

    private final Root<?> root;
    private final CriteriaBuilder cb;

    /**
     * Construct.
     * 
     * @param root the root from which to select from
     * @param criteriaBuilder the criteria builder used internally
     */
    public CriteriaMapper(Root<?> root, CriteriaBuilder criteriaBuilder) {
        this.root = root;
        this.cb = criteriaBuilder;
    }

    /**
     * Creates a joined predicate from the given Filter.
     * 
     * @param filter the filter
     * @return a new JPA criteria Predicate
     */
    public Predicate create(Filter filter) {
        List<Predicate> predicates = new ArrayList<>(filter.getPredicates().size());

        for (org.cdlflex.fruit.Predicate fp : filter.getPredicates()) {
            predicates.add(create(fp));
        }

        return connect(predicates, filter.getConnective());
    }

    /**
     * Maps the given API Predicate object to a JPA criteria Predicate.
     *
     * @param predicate the Predicate object
     * @return a JPA criteria Predicate
     */
    public Predicate create(org.cdlflex.fruit.Predicate predicate) {
        Path<?> attribute = resolvePath(predicate.getKey());
        Object value = predicate.getValue();

        Predicate jpaPredicate = create(predicate.getOp(), attribute, value);

        return (predicate.isNot()) ? jpaPredicate.not() : jpaPredicate;
    }

    private Path<?> resolvePath(String attributeExpression) {
        if (!attributeExpression.contains(".")) {
            // simple attribute
            return root.get(attributeExpression);
        }

        // joined path
        String[] attributes = attributeExpression.split("\\.");

        Path<?> path = root.get(attributes[0]);
        for (int i = 1; i < attributes.length; i++) {
            path = path.get(attributes[i]);
        }
        return path;
    }

    /**
     * Joins the given predicates using the criteria builder with the given connective.
     * 
     * @param predicates the predicates to join
     * @param connective the logical connective
     * @return a new predicate that connects all given predicate
     */
    public Predicate connect(List<Predicate> predicates, Connective connective) {
        Predicate[] predicateArray = predicates.toArray(new Predicate[predicates.size()]);

        switch (connective) {
            case AND:
                return cb.and(predicateArray);
            case OR:
                return cb.or(predicateArray);
            default:
                throw new UnsupportedOperationException("Unknown connective " + connective);
        }
    }

    /**
     * Converts an OrderBy clause to a list of javax.persistence Order instances given a Root and a CriteriaBuilder.
     *
     * @param order the order by clause
     * @return a list of Order instances
     */
    public List<Order> create(OrderBy order) {
        List<Order> list = new ArrayList<>();

        for (SortSpecification sort : order.getSort()) {
            if (sort.getSortOrder() == SortOrder.DESC) {
                list.add(cb.desc(root.get(sort.getKey())));
            } else if (sort.getSortOrder() == SortOrder.ASC) {
                list.add(cb.asc(root.get(sort.getKey())));
            }
        }

        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Predicate create(Operator op, Expression attribute, Object value) {
        switch (op) {
            case EQ:
                return cb.equal(attribute, value);
            case GT:
                return cb.greaterThan(attribute, (Comparable) value);
            case GTE:
                return cb.greaterThanOrEqualTo(attribute, (Comparable) value);
            case LT:
                return cb.lessThan(attribute, (Comparable) value);
            case LTE:
                return cb.lessThanOrEqualTo(attribute, (Comparable) value);
            case LIKE:
                return cb.like(attribute, String.valueOf(value));
            case BETWEEN:
                if (!(value instanceof Range)) {
                    String msg = "BETWEEN operator requires a Range object, was " + value.getClass().getName();
                    throw new IllegalStateException(msg);
                }
                Range range = (Range) value;
                return cb.between(attribute, range.getStart(), range.getEnd());
            default:
                throw new UnsupportedOperationException("Can not translate operator " + op);
        }
    }
}
