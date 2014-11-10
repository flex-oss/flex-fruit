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
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        query.select(cb.count(query.from(getEntityClass())));

        return getEntityManager().createQuery(query);
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

        Predicate where = new JpaCriteriaMapper(from, cb).create(filter);

        return getEntityManager().createQuery(query.select(cb.count(from)).where(where));
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#getAll()} call. In the basic case this is a
     * <code>"SELECT e FROM &lt;Type&gt; e"</code> query for the entity this repository manages.
     *
     * @return a typed query
     */
    public TypedQuery<T> select() {
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        query.from(getEntityClass());

        return getEntityManager().createQuery(query);
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#find(Filter)} call.
     *
     * @param filter the filter
     * @return a typed query
     */
    public TypedQuery<T> select(Filter filter) {
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        Predicate where = new JpaCriteriaMapper(from, cb).create(filter);

        return getEntityManager().createQuery(query.where(where));
    }

    /**
     * Creates a new query that is the basis for the {@link JpaRepository#findPage(Filter, OrderBy, int, int)} call.
     *
     * @param filter the filter
     * @param orderBy the order by clause
     * @return a typed query
     */
    public TypedQuery<T> select(Filter filter, OrderBy orderBy) {
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        JpaCriteriaMapper criteriaMapper = new JpaCriteriaMapper(from, cb);

        return getEntityManager().createQuery(
                query.where(criteriaMapper.create(filter)).orderBy(criteriaMapper.create(orderBy)));
    }

    /**
     * Creates a new basic criteria query with the entity class managed by this repository.
     *
     * @param order the order by clause
     * @return a criteria query
     */
    public TypedQuery<T> select(OrderBy order) {
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        query.orderBy(new JpaCriteriaMapper(from, cb).create(order));

        return getEntityManager().createQuery(query);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
