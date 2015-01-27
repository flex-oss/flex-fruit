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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdlflex.fruit.Repository;
import org.cdlflex.fruit.util.model.Person;
import org.junit.Before;
import org.junit.Test;

public class LongKeyMapRepositoryTest {
    private Repository<Person> repository;

    @Before
    public void setUp() throws Exception {
        repository = new LongKeyMapRepository<>();
    }

    @Test
    public void repository_withExistingMap_constructsRepositoryCorrectly() throws Exception {
        Map<Long, Person> map = new HashMap<>();
        Repository<Person> repo = new LongKeyMapRepository<>(map);
        assertEquals(0, repo.count());
    }

    @Test
    public void repository_withExistingMapThatContainsElements_idGeneratorBehavesCorrectly() throws Exception {
        Map<Long, Person> map = new HashMap<>();

        Person p1 = new Person();
        p1.setId(1L);
        map.put(1L, p1);

        Repository<Person> repo = new LongKeyMapRepository<>(map);
        assertEquals(1, repo.count());

        Person p2 = new Person();
        repo.save(p2);
        assertEquals(new Long(2L), p2.getId());
    }

    @Test
    public void count_afterSave_incrementsCorrectly() throws Exception {
        Person e1 = new Person();
        Person e2 = new Person();
        Person e3 = new Person();

        assertEquals(0, repository.count());
        repository.save(e1);
        assertEquals(1, repository.count());
        repository.save(e2);
        repository.save(e3);
        assertEquals(3, repository.count());
    }

    @Test
    public void save_createsId() throws Exception {
        Person entity = new Person();
        repository.save(entity);
        assertNotNull(entity.getId());
        assertEquals(new Long(1), entity.getId());
    }

    @Test
    public void save_collection_createsIds() throws Exception {
        Person e1 = new Person();
        Person e2 = new Person();
        Person e3 = new Person();

        repository.save(Arrays.asList(e1, e2, e3));

        assertEquals(new Long(1), e1.getId());
        assertEquals(new Long(2), e2.getId());
        assertEquals(new Long(3), e3.getId());
    }

    @Test
    public void remove_removesEntity() throws Exception {
        Person e1 = new Person();

        repository.save(e1);
        repository.remove(e1);

        assertNull(repository.get(1L));
    }

    @Test
    public void remove_collection_removesEntities() throws Exception {
        Person e1 = new Person();
        Person e2 = new Person();
        Person e3 = new Person();

        repository.save(Arrays.asList(e1, e2, e3));

        repository.remove(Arrays.asList(e1, e3));

        assertNull(repository.get(1L));
        assertSame(e2, repository.get(2L));
        assertNull(repository.get(3L));
    }

    @Test
    public void get_nonExistingElement_returnsNull() throws Exception {
        assertNull(repository.get(1L));
    }

    @Test
    public void get_afterSave_returnsElement() throws Exception {
        Person entity = new Person();
        repository.save(entity);
        assertSame(entity, repository.get(1L));
    }

    @Test
    public void getAll_afterSave_returnsElements() throws Exception {
        Person e1 = new Person();
        Person e2 = new Person();
        Person e3 = new Person();

        repository.save(Arrays.asList(e1, e2, e3));

        List<Person> all = repository.getAll();
        assertEquals(3, all.size());
        assertTrue(all.contains(e1));
        assertTrue(all.contains(e2));
        assertTrue(all.contains(e3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void create_throwsException() throws Exception {
        repository.create();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void nativeQuery_notImplemented() throws Exception {
        repository.nativeQuery(new Object());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void nativeListQuery_notImplemented() throws Exception {
        repository.nativeListQuery(new Object());
    }

}
