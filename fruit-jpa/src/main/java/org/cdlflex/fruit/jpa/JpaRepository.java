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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Identifiable;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.PersistenceException;
import org.cdlflex.fruit.Query;
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

    private EntityManager entityManager;

    /**
     * The type of entities being managed
     */
    private final Class<T> entityClass;

    private TransactionType transactionType;

    private QueryFactory<T> queryFactory;

    public JpaRepository(Class<T> entityClass) {
        this(entityClass, TransactionType.RESOURCE_LOCAL);
    }

    public JpaRepository(Class<T> entityClass, TransactionType transactionType) {
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass can not be null");
        }

        this.entityClass = entityClass;
        this.transactionType = transactionType;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Lazy-init methods for a {@link org.cdlflex.fruit.jpa.QueryFactory} instance using the EntityManager and entity
     * type of this repository.
     * 
     * @return a QueryFactory instance
     */
    protected QueryFactory<T> getQueryFactory() {
        if (queryFactory == null) {
            queryFactory = new QueryFactory<>(getEntityClass(), getEntityManager());
        }

        return queryFactory;
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
            return getQueryFactory().count().getSingleResult();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public long count(Filter filter) {
        try {
            return getQueryFactory().count(filter).getSingleResult();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public T get(Object id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    @Override
    public List<T> getAll() {
        try {
            return getQueryFactory().select().getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> getAll(OrderBy order) {
        try {
            return getQueryFactory().select(order).getResultList();
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> find(Query query) {
        TypedQuery<T> q = getQueryFactory().select(query);
        return q.getResultList();
    }

    @Override
    public Object nativeQuery(Object query) throws UnsupportedOperationException {
        try {
            if (query instanceof javax.persistence.Query) {
                return ((javax.persistence.Query) query).getResultList();
            } else if (query instanceof CriteriaQuery) {
                return nativeQuery(getEntityManager().createQuery((CriteriaQuery) query));
            } else if (query instanceof String) {
                return nativeQuery(entityManager.createQuery((String) query));
            } else {
                throw new UnsupportedOperationException("Can not dispatch queries of type " + query.getClass());
            }
        } catch (javax.persistence.PersistenceException | IllegalArgumentException e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> nativeListQuery(Object query) throws UnsupportedOperationException {
        return (List<T>) nativeQuery(query);
    }

    /**
     * Finds an entity by a given attribute. Returns null if none was found.
     *
     * @param attribute the attribute to search for
     * @param value the value
     * @return the entity or null if none was found
     */
    public T findOneByAttribute(String attribute, Object value) {
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

    @Override
    public void save(final T entity) {
        execute(new EntityManagerCommand() {
            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                onBeforePersist(entity);
                if (!em.contains(entity)) {
                    em.persist(entity);
                } else {
                    em.flush();
                }
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
            boolean flush = false;

            @Override
            public void execute(EntityManager em, EntityTransaction tx) {
                for (T e : entities) {
                    onBeforePersist(e);
                    if (!em.contains(e)) {
                        em.persist(e);
                    } else {
                        flush = true;
                    }
                }

                if (flush) {
                    em.flush();
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

    /**
     * Executes the given command using an EntityManagerCommandExecutor.
     *
     * @param command the command to execute
     */
    protected void execute(EntityManagerCommand command) {
        try {
            getEntityManagerCommandExecutor().execute(command);
        } catch (javax.persistence.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    protected EntityManagerCommandExecutor getEntityManagerCommandExecutor() {
        switch (getTransactionType()) {
            case JTA:
                return new DefaultEntityManagerCommandExecutor(getEntityManager());
            case RESOURCE_LOCAL:
            default:
                return new TransactionalEntityManagerCommandExecutor(getEntityManager());
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

}
