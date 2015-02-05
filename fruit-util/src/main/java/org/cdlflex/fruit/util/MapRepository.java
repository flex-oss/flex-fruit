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
package org.cdlflex.fruit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Identifiable;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.Query;
import org.cdlflex.fruit.Repository;

/**
 * A Repository implementation using a Map as object registry.
 *
 * @param <K> The key type
 * @param <T> The entity type
 */
public abstract class MapRepository<K, T extends Identifiable<K>> implements Repository<T> {

    private Map<K, T> registry;

    /**
     * Creates a new MapRepository that uses a LinkedHashMap by default.
     */
    public MapRepository() {
        this(new LinkedHashMap<K, T>());
    }

    public MapRepository(Map<K, T> registry) {
        this.registry = registry;
    }

    @Override
    public long count() {
        return registry.size();
    }

    @Override
    public long count(Filter filter) {
        return find(new Query(filter)).size();
    }

    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(nextKey(entity));
        }

        registry.put(entity.getId(), entity);
    }

    @Override
    public void save(Collection<T> entities) {
        for (T e : entities) {
            save(e);
        }
    }

    @Override
    public T get(Object id) {
        return registry.get(id);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(registry.values());
    }

    @Override
    public List<T> getAll(OrderBy order) {
        List<T> all = getAll();
        sort(all, order);
        return all;
    }

    @Override
    public List<T> find(Query query) {
        List<T> all = getAll();
        if (query.getFilter() != null) {
            retain(all, query.getFilter());
        }
        if (query.getOrderBy() != null) {
            sort(all, query.getOrderBy());
        }

        Integer limit = query.getLimit();
        Integer offset = query.getOffset();

        if (offset != null) {
            if (limit != null) {
                return all.subList(offset, offset + limit);
            } else {
                return all.subList(offset, all.size());
            }
        } else {
            if (limit != null) {
                return all.subList(0, limit);
            }
        }

        return all;
    }

    @Override
    public T create() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object nativeQuery(Object query) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("There is no native query for MapRepositories");
    }

    @Override
    public List<T> nativeListQuery(Object query) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("There is no native query for MapRepositories");
    }

    @Override
    public void remove(T entity) {
        registry.remove(entity.getId());
    }

    @Override
    public void remove(Collection<T> entities) {
        for (T entity : entities) {
            remove(entity);
        }
    }

    /**
     * Sorts the given list by the given OrderBy clause (as far as possible).
     * 
     * @param list the list to sort
     * @param order the specification by which to sort
     */
    protected void sort(List<T> list, OrderBy order) {
        // TODO: implement
    }

    /**
     * Retains all elements in the given list that satisfy the given filter.
     *
     * @param list the list to filter
     * @param filter the filter
     */
    protected void retain(List<T> list, Filter filter) {
        // TODO: implement
    }

    /**
     * Factory method for a new key for the given entity.
     *
     * @param entity the entity for which to create the key
     * @return a new key
     */
    protected abstract K nextKey(T entity);

}
