package org.cdlflex.fruit;

import java.util.List;

/**
 * Interface that provides generic finder methods for a DAO.
 *
 * @param <T> The specific ModelEntity type
 */
public interface Finder<T extends Identifiable<?>> {

    /**
     * Returns the amount of entities this repository manages.
     *
     * @return the amount of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    long count();

    /**
     * Returns the amount of entities that satisfy the given filter.
     *
     * @param filter the filter
     * @return the amount of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    long count(Filter filter);

    /**
     * Returns the entity with the given id.
     *
     * @param id the entity id
     * @return the entity with the given id
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    T get(Object id);

    /**
     * Returns all entities managed by this repository.
     *
     * @return a list of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> getAll();

    /**
     * Returns all entities managed by this repository.
     *
     * @param order the order by specification
     * @return a list of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> getAll(OrderBy order);

    /**
     * Returns a limited amount of entities managed by this repository.
     *
     * @param limit the amount of entities that should be returned
     * @param offset the starting index
     * @return a page of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> getPage(int limit, int offset);

    /**
     * Returns a limited amount of entities managed by this repository.
     *
     * @param order the order by specification
     * @param limit the amount of entities that should be returned
     * @param offset the starting index
     * @return a page of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> getPage(OrderBy order, int limit, int offset);

    /**
     * Returns all entries that satisfy the given filter.
     *
     * @param filter the filter
     * @return a list of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> find(Filter filter);

    /**
     * Returns all entries that satisfy the given filter.
     *
     * @param filter the filter
     * @param order the order by specification
     * @param limit the amount of entities that should be returned
     * @param offset the starting index
     * @return a page of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    // CHECKSTYLE:OFF query API will be abstracted in the future
    List<T> findPage(Filter filter, OrderBy order, int limit, int offset);
    // CHECKSTYLE: ON
}
