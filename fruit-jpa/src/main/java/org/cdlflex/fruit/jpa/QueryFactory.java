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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.Query;

/**
 * Creates {@code javax.persistence.TypedQuery} instances for a given entity type.
 * 
 * @param <T> the entity type
 */
public class QueryFactory<T> {

    private Class<T> entityClass;
    private EntityManager entityManager;

    private CriteriaBuilder cb;

    public QueryFactory(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder();
    }

    /**
     * Creates a new TypedQuery that queries the amount of entities of the entity class of this QueryFactory.
     *
     * @return a query
     */
    public TypedQuery<Long> count() {
        return count(null);
    }

    /**
     * Creates a new TypedQuery that queries the amount of entities of the entity class of this QueryFactory, that a
     * query for the given Filter would return.
     *
     * @param filter the filter
     * @return a query
     */
    public TypedQuery<Long> count(Filter filter) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> from = query.from(getEntityClass());

        if (filter != null) {
            Predicate where = new CriteriaMapper(from, cb).create(filter);
            query.where(where);
        }

        return getEntityManager().createQuery(query.select(cb.count(from)));
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#getAll()} call. In the basic case this is a
     * <code>"SELECT e FROM &lt;Type&gt; e"</code> query for the entity this repository manages.
     *
     * @return a typed query
     */
    public TypedQuery<T> select() {
        return select(null, null);
    }

    /**
     * Creates a new basic criteria query with the entity class managed by this repository.
     *
     * @param order the order by clause
     * @return a criteria query
     */
    public TypedQuery<T> select(OrderBy order) {
        return select(null, order);
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#find(org.cdlflex.fruit.Query)} call without
     * limit and offset.
     *
     * @param filter the filter
     * @param orderBy the order by clause
     * @return a typed query
     */
    public TypedQuery<T> select(Filter filter, OrderBy orderBy) {
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        CriteriaMapper criteriaMapper = new CriteriaMapper(from, cb);

        if (orderBy != null) {
            query.orderBy(criteriaMapper.create(orderBy));
        }
        if (filter != null) {
            query.where(criteriaMapper.create(filter));
        }

        return getEntityManager().createQuery(query);
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#find(org.cdlflex.fruit.Query)} call. Depending
     * on which parameters are set in the {@link org.cdlflex.fruit.Query} object, the query {@code TypedQuery} is built
     * dynamically.
     * 
     * @param query the fruit query
     * @return a jpa query
     */
    public TypedQuery<T> select(Query query) {
        TypedQuery<T> q = select(query.getFilter(), query.getOrderBy());

        Integer limit = query.getLimit();
        Integer offset = query.getOffset();

        if (limit != null) {
            q.setMaxResults(limit);
        }
        if (offset != null) {
            q.setFirstResult(offset);
        }

        return q;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
