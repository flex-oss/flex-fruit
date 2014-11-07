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

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Identifiable;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.PersistenceException;
import org.cdlflex.fruit.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic JpaRepository that uses an EntityManager to manage ModelEntities.
 *
 * @param <T> The specific ModelEntity type
 */
public class JpaRepository<T extends Identifiable<?>> implements Repository<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JpaRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * The type of entities being managed
     */
    private final Class<T> entityClass;

    public JpaRepository(Class<T> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass can not be null");
        }

        this.entityClass = entityClass;
    }

    @Override
    public T create() {
        try {
            return getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("Failed to create entity of type {}", getEntityClass(), e);
            return null;
        }
    }

    @Override
    public long count() {
        try {
            return createCountQuery().getSingleResult();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public long count(Filter filter) {
        try {
            return getEntityManager().createQuery(translateCountQuery(filter)).getSingleResult();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void save(final T entity) {
        execute(new EntityManagerCommand() {
            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                onBeforePersist(entity);
                em.persist(entity);
            }

            @Override
            public void onAfterCommit(EntityManager em, EntityTransaction tx) {
            }

            @Override
            public void onException(EntityManager em, EntityTransaction tx, Exception e) {
                LOG.error("Error while persisting entity {}", entity, e);
            }
        });
    }

    @Override
    public void save(final Collection<T> entities) {
        execute(new EntityManagerCommand() {
            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                for (T e : entities) {
                    onBeforePersist(e);
                    em.persist(e);
                }
            }

            @Override
            public void onAfterCommit(EntityManager em, EntityTransaction tx) {
            }

            @Override
            public void onException(EntityManager em, EntityTransaction tx, Exception e) {
                LOG.error("Error while persisting {} entities", entities.size(), e);
            }
        });
    }

    @Override
    public void remove(final T entity) {
        execute(new EntityManagerCommand() {
            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                em.remove(entity);
                onAfterRemove(entity);
            }

            @Override
            public void onAfterCommit(EntityManager em, EntityTransaction tx) {
            }

            @Override
            public void onException(EntityManager em, EntityTransaction tx, Exception e) {
                LOG.error("Error while removing entity {}", entity, e);
            }
        });
    }

    @Override
    public void remove(final Collection<T> entities) {
        execute(new EntityManagerCommand() {
            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                for (T entity : entities) {
                    em.remove(entity);
                    onAfterRemove(entity);
                }
            }

            @Override
            public void onAfterCommit(EntityManager em, EntityTransaction tx) {
            }

            @Override
            public void onException(EntityManager em, EntityTransaction tx, Exception e) {
                LOG.error("Error while removing {} entities", entities.size(), e);
            }
        });
    }

    @Override
    public T get(Object id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    @Override
    public List<T> getAll() {
        try {
            return createBasicQuery().getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> getAll(OrderBy order) {
        try {
            return getEntityManager().createQuery(createBasicCriteriaQuery(order)).getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> getPage(int limit, int offset) {
        try {
            TypedQuery<T> q = createBasicQuery();
            q.setFirstResult(offset).setMaxResults(limit);
            return q.getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> getPage(OrderBy order, int limit, int offset) {
        try {
            TypedQuery<T> q = getEntityManager().createQuery(createBasicCriteriaQuery(order));
            q.setFirstResult(offset).setMaxResults(limit);
            return q.getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> find(Filter filter) {
        try {
            return getEntityManager().createQuery(translateQuery(filter)).getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    // CHECKSTYLE:OFF query api will be abstracted in the future
    public List<T> findPage(Filter filter, OrderBy order, int limit, int offset) {
        // CHECKSTYLE:ON
        try {
            TypedQuery<T> query = getEntityManager().createQuery(translateQuery(filter, order));
            return query.setFirstResult(offset).setMaxResults(limit).getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Converts the given Filter to a criteria query.
     *
     * @param filter the filter
     * @return a new JPA CriteriaQuery
     */
    protected CriteriaQuery<T> translateQuery(Filter filter) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        Predicate where = new JpaCriteriaMapper(from, cb).create(filter);

        return query.where(where);
    }

    /**
     * Converts the given Filter to a criteria query.
     *
     * @param filter the filter
     * @param orderBy the order by clause
     * @return a new JPA CriteriaQuery
     */
    protected CriteriaQuery<T> translateQuery(Filter filter, OrderBy orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        JpaCriteriaMapper criteriaMapper = new JpaCriteriaMapper(from, cb);

        return query.where(criteriaMapper.create(filter)).orderBy(criteriaMapper.create(orderBy));
    }

    /**
     * Converts the given Filter to a criteria query.
     *
     * @param filter the filter
     * @return a new JPA CriteriaQuery
     */
    protected CriteriaQuery<Long> translateCountQuery(Filter filter) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> from = query.from(getEntityClass());

        Predicate where = new JpaCriteriaMapper(from, cb).create(filter);

        return query.select(cb.count(from)).where(where);
    }

    /**
     * Finds an entity by a given attribute. Returns null if none was found.
     *
     * @param attribute the attribute to search for
     * @param value the value
     * @return the entity or null if none was found
     */
    protected T findOneByAttribute(String attribute, Object value) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> from = query.from(getEntityClass());

        query.where(cb.equal(from.get(attribute), value));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Called before an entity is persisted to the entity manager.
     *
     * @param entity the entity being persisted
     */
    protected void onBeforePersist(T entity) {
        // hook
    }

    /**
     * Called after an entity was removed from the entity manager.
     *
     * @param entity the entity being removed
     */
    protected void onAfterRemove(T entity) {
        // hook
    }

    /**
     * Executes the given command using an EntityManagerCommandExecutor.
     *
     * @param command the command to execute
     */
    protected void execute(EntityManagerCommand command) {
        try {
            new EntityManagerCommandExecutor(getEntityManager()).execute(command);
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Creates a new TypedQuery for the given qlString.
     *
     * @param qlString the query
     * @return a TypedQuery
     */
    protected TypedQuery<T> createQuery(String qlString) {
        return getEntityManager().createQuery(qlString, getEntityClass());
    }

    /**
     * Creates a new TypedQuery for the given qlString and sets the given parameters in the same order.
     *
     * @param qlString the query
     * @param parameters the parameters to set
     * @return a TypedQuery
     */
    protected TypedQuery<T> createQuery(String qlString, Object... parameters) {
        TypedQuery<T> query = createQuery(qlString);

        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }

        return query;
    }

    /**
     * Creates a new TypedQuery that queries the amount of entities this repository manages.
     *
     * @return a query
     */
    protected TypedQuery<Long> createCountQuery() {
        return createCountQuery(getEntityClass());
    }

    /**
     * Creates a new TypedQuery that queries the amount of entities of the given entity class.
     *
     * @param entityType the entity class to be queries
     * @return a query
     */
    protected TypedQuery<Long> createCountQuery(Class<T> entityType) {
        return entityManager.createQuery("SELECT COUNT(e) FROM " + entityType.getSimpleName() + " e", Long.class);
    }

    /**
     * Creates a new query that is the basis for the {@link #getAll()} call. In the basic case this is a
     * <code>"SELECT e FROM &lt;Type&gt; e"</code> query for the entity this repository manages.
     *
     * @return a typed query
     */
    protected TypedQuery<T> createBasicQuery() {
        return createBasicQuery(getEntityClass());
    }

    /**
     * Creates a new query that is the basis for the {@link #getAll()} call for the given entity type. In the basic case
     * this is a <code>"SELECT e FROM &lt;Type&gt; e"</code> query for the given entity type.
     *
     * @param entityType the entity class to be queried
     * @param <E> the entity class type
     * @return a typed query
     */
    protected <E> TypedQuery<E> createBasicQuery(Class<E> entityType) {
        return entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType);
    }

    /**
     * Creates a new basic criteria query with the entity class managed by this repository.
     *
     * @param order the order by clause
     * @return a criteria query
     */
    protected CriteriaQuery<T> createBasicCriteriaQuery(OrderBy order) {
        return createBasicCriteriaQuery(getEntityClass(), order);
    }

    /**
     * Creates a new basic criteria query for the given entity class.
     *
     * @param entityType the entity type
     * @param order the order by clause
     * @param <E> the entity type
     * @return a criteria query
     */
    protected <E> CriteriaQuery<E> createBasicCriteriaQuery(Class<E> entityType, OrderBy order) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityType);
        Root<E> from = query.from(entityType);

        query.orderBy(new JpaCriteriaMapper(from, cb).create(order));

        return query;
    }

}
