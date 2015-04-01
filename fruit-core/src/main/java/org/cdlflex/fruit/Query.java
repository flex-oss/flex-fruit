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

/**
 * A Query object represents a query that can be used to find entities in a Repository. It may contain a
 * {@link org.cdlflex.fruit.Filter} ({@code WHERE} clause) an {@link org.cdlflex.fruit.OrderBy} object ({@code ORDER}
 * clause), and limit and offset.
 * 
 * Null values of these parameters are considered as not set, e.g. a Query without a Filter object will not contain a
 * {@code WHERE} clause, but still be accepted by {@link org.cdlflex.fruit.Repository#find(Query)}.
 */
public class Query implements Serializable {

    private Filter filter;
    private OrderBy orderBy;
    private Integer limit;
    private Integer offset;

    public Query() {
    }

    public Query(Filter filter) {
        this(filter, null, null, null);
    }

    public Query(Filter filter, Integer limit) {
        this(filter, null, limit, null);
    }

    public Query(Filter filter, Integer limit, Integer offset) {
        this(filter, null, limit, offset);
    }

    public Query(Filter filter, OrderBy orderBy) {
        this(filter, orderBy, null, null);
    }

    public Query(Filter filter, OrderBy orderBy, Integer limit) {
        this(filter, orderBy, limit, null);
    }

    public Query(OrderBy orderBy) {
        this(null, orderBy, null, null);
    }

    public Query(OrderBy orderBy, Integer limit, Integer offset) {
        this(null, orderBy, limit, offset);
    }

    public Query(Integer limit, Integer offset) {
        this(null, null, limit, offset);
    }

    public Query(Filter filter, OrderBy orderBy, Integer limit, Integer offset) {
        this.filter = filter;
        this.orderBy = orderBy;
        this.limit = limit;
        this.offset = offset;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
