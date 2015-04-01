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
     * Returns all entities that satisfy the given query.
     * 
     * @param query the query
     * @return a list of entities
     * @throws PersistenceException if an exception occurs in the underlying persistence system
     */
    List<T> find(Query query);

    /**
     * Dispatches a query in the form of whatever the underlying implementation accepts. This may vary greatly for
     * various implementation providers. E.g. for a JDBC implementation, the query object could be an SQL string.
     * 
     * The return value is also an implementation specific representation of a result. In a JDBC implementation, this
     * could be a {@code ResultSet}.
     * 
     * @param query a query object
     * @return a query result
     * @throws UnsupportedOperationException if the given query object type is not supported by the provider
     */
    Object nativeQuery(Object query) throws UnsupportedOperationException;

    /**
     * Dispatches a native query and returns a list of results as mapped entities.
     *
     * @see #nativeQuery(Object)
     * @param query a query object
     * @return a list of entities
     * @throws UnsupportedOperationException if the given query object type is not supported by the provider
     */
    List<T> nativeListQuery(Object query) throws UnsupportedOperationException;
}
