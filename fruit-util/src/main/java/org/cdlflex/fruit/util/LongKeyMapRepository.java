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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.cdlflex.fruit.Identifiable;

/**
 * MapRepository that uses Long values as keys. IDs are generated using an AtomicLong incremental counter.
 *
 * @param <T> The entity type
 */
public class LongKeyMapRepository<T extends Identifiable<Long>> extends MapRepository<Long, T> {

    private AtomicLong idGenerator;

    public LongKeyMapRepository() {
        super();
        idGenerator = new AtomicLong();
    }

    public LongKeyMapRepository(Map<Long, T> registry) {
        super(registry);

        // find the highest key value already in the registry and start iterating from there
        idGenerator = new AtomicLong(Collections.max(registry.keySet()));
    }

    @Override
    protected Long nextKey(T entity) {
        return idGenerator.incrementAndGet();
    }

}
