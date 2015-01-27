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

/**
 * A basic DAO-type repository that manages ModelEntities.
 *
 * @param <T> The specific ModelEntity type
 */
public interface Repository<T extends Identifiable<?>> extends Finder<T> {
    /**
     * Creates a new instance of the managed Entity. This just invokes the constructor, it does not actually persist
     * anything. This allows the repository to act as a factory, e.g. for entities that have been abstracted into
     * interfaces.
     *
     * @return a new instance of T
     */
    T create();

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

}
