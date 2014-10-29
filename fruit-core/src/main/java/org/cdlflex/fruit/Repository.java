package org.cdlflex.fruit;

import java.util.Collection;
import java.util.List;

/**
 * A basic repository that manages ModelEntities.
 *
 * @param <T> The specific ModelEntity type
 */
public interface Repository<T extends Identifiable<?>> {
    /**
     * Creates a new instance of the managed Entity. This just invokes the constructor, it does not actually persist
     * anything.
     *
     * @return a new instance of T
     */
    T create();

    /**
     * Returns the amount of entities this repository manages.
     *
     * @return the amount of entities
     */
    long count();

    /**
     * Returns the amount of entities that satisfy the given filter.
     *
     * @param filter the filter
     * @return the amount of entities
     */
    long count(Filter filter);

    /**
     * Persists the given entity. This is an "upersert" command.
     *
     * @param entity the entity to persist
     */
    void save(T entity);

    /**
     * Persists the given collection of entities. This is an "upersert" command.
     *
     * @param entity the entities to persist
     */
    void save(Collection<T> entity);

    /**
     * Returns the entity with the given id.
     *
     * @param id the entity id
     * @return the entity with the given id
     */
    T get(Object id);

    /**
     * Returns all entities managed by this repository.
     *
     * @return a list of entities
     */
    List<T> getAll();

    /**
     * Returns all entities managed by this repository.
     *
     * @param order the order by specification
     * @return a list of entities
     */
    List<T> getAll(OrderBy order);

    /**
     * Returns a limited amount of entities managed by this repository.
     *
     * @param limit the amount of entities that should be returned
     * @param offset the starting index
     * @return a page of entities
     */
    List<T> getPage(int limit, int offset);

    /**
     * Returns a limited amount of entities managed by this repository.
     *
     * @param order the order by specification
     * @param limit the amount of entities that should be returned
     * @param offset the starting index
     * @return a page of entities
     */
    List<T> getPage(OrderBy order, int limit, int offset);

    /**
     * Returns all entries that satisfy the given filter.
     *
     * @param filter the filter
     * @return a list of entities
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
     */
    // CHECKSTYLE:OFF query API will be abstracted in the future
    List<T> findPage(Filter filter, OrderBy order, int limit, int offset);
    // CHECKSTYLE: ON
}
