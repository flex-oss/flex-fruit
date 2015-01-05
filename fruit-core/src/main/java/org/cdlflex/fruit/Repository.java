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
     * Persists the given entity. This is an upsert/merge command (i.e. creates non-persistent and updates existing
     * entities).
     *
     * @param entity the entity to persist
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    void save(T entity);

    /**
     * Persists the given collection of entities. This is an upsert/merge command (i.e. creates non-persistent and
     * updates existing entities).
     *
     * @param entity the entities to persist
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    void save(Collection<T> entity);

    /**
     * Removes the given entity from the persistence structure.
     * 
     * @param entity the entity to remove
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    void remove(T entity);

    /**
     * Removes all given entities from the persistence structure.
     * 
     * @param entities the entities to remove
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    void remove(Collection<T> entities);

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
