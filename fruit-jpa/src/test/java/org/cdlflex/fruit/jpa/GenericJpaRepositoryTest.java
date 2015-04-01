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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.cdlflex.fruit.Connective;
import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Operator;
import org.cdlflex.fruit.OrderBy;
import org.cdlflex.fruit.PersistenceException;
import org.cdlflex.fruit.Predicate;
import org.cdlflex.fruit.Query;
import org.cdlflex.fruit.Range;
import org.cdlflex.fruit.SortOrder;
import org.cdlflex.fruit.jpa.model.ManagedEntity;
import org.cdlflex.fruit.jpa.model.NoConstructorModel;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public abstract class GenericJpaRepositoryTest<E extends ManagedEntity, R extends JpaRepository<E>> extends
        AbstractJpaTest {

    private R repository;

    @Before
    public void setUpRepository() throws Exception {
        repository = newRepository();
        repository.setEntityManager(getEntityManager());
    }

    @Test
    public void repository_wasCreated() throws Exception {
        assertNotNull(repository);
    }

    @Test
    public void create_createsNewEntityOfCorrectClass() throws Exception {
        E e = repository.create();

        assertEquals(repository.getEntityClass(), e.getClass());
    }

    @Test
    public void save_setsIdCorrectly() throws Exception {
        E e = repository.create();

        repository.save(e);
        assertNotNull(e.getId());
        assertThat(e.getId(), is(1L));
    }

    @Test
    public void save_collection_setsIdsCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));
        assertNotNull(e1.getId());
        assertNotNull(e2.getId());

        assertThat(e1.getId(), is(1L));
        assertThat(e2.getId(), is(2L));
    }

    @Test
    public void remove_removesEntityFromContext() throws Exception {
        E e1 = repository.create();

        EntityTransaction tx = getEntityManager().getTransaction();
        tx.begin();
        getEntityManager().persist(e1);
        tx.commit();

        Long id = e1.getId();
        assertNotNull(getEntityManager().find(repository.getEntityClass(), id));
        repository.remove(e1);
        assertNull(getEntityManager().find(repository.getEntityClass(), id));
    }

    @Test
    public void remove_collection_removesEntityFromContext() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        EntityTransaction tx = getEntityManager().getTransaction();
        tx.begin();
        getEntityManager().persist(e1);
        getEntityManager().persist(e2);
        getEntityManager().persist(e3);
        tx.commit();

        assertNotNull(getEntityManager().find(repository.getEntityClass(), e1.getId()));
        assertNotNull(getEntityManager().find(repository.getEntityClass(), e2.getId()));
        assertNotNull(getEntityManager().find(repository.getEntityClass(), e3.getId()));
        repository.remove(Arrays.asList(e1, e2));
        assertNull(getEntityManager().find(repository.getEntityClass(), e1.getId()));
        assertNull(getEntityManager().find(repository.getEntityClass(), e2.getId()));
        assertNotNull(getEntityManager().find(repository.getEntityClass(), e3.getId()));
    }

    @Test
    public void get_returnsCorrectEntity() throws Exception {
        E e = repository.create();

        repository.save(e);

        E e1 = repository.get(1L);
        assertThat(e1, is(e));
    }

    @Test
    public void getAll_returnsAllEntities() throws Exception {
        List<E> all = repository.getAll();
        assertThat(all.size(), is(0));

        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));

        all = repository.getAll();
        assertThat(all.size(), is(2));
        assertThat(all, hasItem(e1));
        assertThat(all, hasItem(e2));
    }

    @Test
    public void getAll_orderBy_returnsAllEntitiesInCorrectOrder() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3));

        List<E> result = repository.getAll(new OrderBy("id", SortOrder.DESC));

        assertThat(result.size(), is(3));
        assertEquals(e3, result.get(0));
        assertEquals(e2, result.get(1));
        assertEquals(e1, result.get(2));
    }

    @Test
    public void getPage_withOrderBy_returnsEntitiesInCorrectOrder() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3));

        Query query = new Query(new OrderBy("id", SortOrder.DESC), 2, 1);

        List<E> result = repository.find(query);

        assertThat(result.size(), is(2));
        assertEquals(e2, result.get(0));
        assertEquals(e1, result.get(1));
    }

    @Test
    public void getPage_returnsCorrectEntities() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3, e4));

        List<E> page = repository.find(new Query(2, 1));

        assertThat(page.size(), is(2));
        assertThat(page, hasItem(e2));
        assertThat(page, hasItem(e3));
    }

    @Test
    public void count_returnsCorrectAmount() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();

        repository.save(Arrays.asList(e1, e2));

        assertThat(repository.count(), is(2L));

        repository.save(Arrays.asList(e3, e4));

        assertThat(repository.count(), is(4L));
    }

    @Test
    public void count_withFilter_returnsCorrectAmount() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3, e4));

        assertEquals(0, repository.count(new Filter(new Predicate("id", "<", 1))));
        assertEquals(1, repository.count(new Filter(new Predicate("id", "=", 2))));
        assertEquals(2, repository.count(new Filter(new Predicate("id", ">", 2))));
        assertEquals(3, repository.count(new Filter(new Predicate("id", ">=", 2))));
    }

    @Test
    public void findByFilter_eq_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3));

        Filter filter = new Filter().add("id", Operator.EQ, 2L);
        List<E> result = getRepository().find(new Query(filter));

        assertThat(result.size(), is(1));
        assertThat(result, hasItems(e2));
    }

    @Test
    public void findByFilter_eq_not_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3));

        Filter filter = new Filter().add(new Predicate("id", Operator.EQ, 2L).not());
        List<E> result = getRepository().find(new Query(filter));

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(e1, e3));
    }

    @Test
    public void findByFilter_or_eq_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3));

        Filter filter = new Filter(Connective.OR).add(new Predicate("id", "=", 1L)).add(new Predicate("id", 2L));
        List<E> result = getRepository().find(new Query(filter));

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(e1, e2));
    }

    @Test
    public void findByFilter_between_valueInRange_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3, e4));

        Filter filter = new Filter().add(new Predicate("id", Operator.BETWEEN, new Range<>(5L, 7L)));
        List<E> result = getRepository().find(new Query(filter));
        assertThat(result.size(), is(0));
    }

    @Test(expected = IllegalStateException.class)
    public void findByFilter_between_nonRangeObject_throwsException() throws Exception {
        Filter filter = new Filter().add(new Predicate("id", Operator.BETWEEN, 0));
        getRepository().find(new Query(filter));
    }

    @Test
    public void findByFilter_between_valueNotInRange_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3, e4));

        Filter filter = new Filter().add(new Predicate("id", Operator.BETWEEN, new Range<>(1L, 3L)));
        List<E> result = getRepository().find(new Query(filter));
        assertThat(result.size(), is(3));
        assertThat(result, hasItems(e1, e2, e3));
    }

    @Test
    public void findPageWithFilter_behavesCorrectly() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();
        E e3 = repository.create();
        E e4 = repository.create();
        E e5 = repository.create();

        repository.save(Arrays.asList(e1, e2, e3, e4, e5));

        Filter filter = new Filter().add("id", Operator.GT, 2L);
        List<E> result = getRepository().find(new Query(filter, new OrderBy("id", SortOrder.DESC), 2, 1));

        assertEquals(2, result.size());
        assertEquals(e4, result.get(0));
        assertEquals(e3, result.get(1));
    }

    @Test
    public void findOneByAttribute_returnsCorrectResult() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));

        E obj = repository.findOneByAttribute("id", 2L);
        assertEquals(e2, obj);
    }

    @Test
    public void findOneByAttribute_existingAttribute_nonExistingValue_returnsNull() throws Exception {
        E e1 = repository.create();

        repository.save(e1);

        assertNull(repository.findOneByAttribute("id", 2L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findOneByAttribute_nonExistingAttribute_throwsIllegalArgumentException() throws Exception {
        try {
            repository.save(repository.create());
        } catch (IllegalArgumentException e) {
            fail("Exception caught too early");
        }

        assertNull(repository.findOneByAttribute("doesnotexist", 1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRepository_withNullClass_throwsException() throws Exception {
        new JpaRepository<>(null);
    }

    @Test
    public void create_nonExistingDefaultConstructor_returnsNull() throws Exception {
        JpaRepository<NoConstructorModel> rep = new JpaRepository<>(NoConstructorModel.class);

        assertNull(rep.create());
    }

    @Test
    public void nativeQuery_qlString_returnsCorrectResult() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));
        String ql = "SELECT e FROM " + repository.getEntityClass().getSimpleName() + " e WHERE e.id=2";

        Object resultObject = repository.nativeQuery(ql);
        assertThat(resultObject, is(instanceOf(List.class)));

        List<E> resultList = (List<E>) resultObject;
        assertEquals(1, resultList.size());
        assertEquals(2L, (long) resultList.get(0).getId());
    }

    @Test
    public void nativeListQuery_qlString_returnsCorrectResult() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));
        String ql = "SELECT e FROM " + repository.getEntityClass().getSimpleName() + " e WHERE e.id=2";

        List<E> resultList = repository.nativeListQuery(ql);

        assertEquals(1, resultList.size());
        assertEquals(2L, (long) resultList.get(0).getId());
    }

    @Test
    public void nativeQuery_typedQuery_returnsCorrectResult() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));
        Object query =
            getEntityManager().createQuery(
                    "SELECT e FROM " + repository.getEntityClass().getSimpleName() + " e WHERE e.id = 2");

        Object resultObject = repository.nativeQuery(query);
        assertThat(resultObject, is(instanceOf(List.class)));

        List<E> resultList = (List<E>) resultObject;
        assertEquals(1, resultList.size());
        assertEquals(2L, (long) resultList.get(0).getId());
    }

    @Test
    public void nativeQuery_criteriaQuery_returnsCorrectResult() throws Exception {
        E e1 = repository.create();
        E e2 = repository.create();

        repository.save(Arrays.asList(e1, e2));

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(repository.getEntityClass());
        Root<E> from = query.from(repository.getEntityClass());
        query.where(cb.equal(from.get("id"), 2L));

        Object resultObject = repository.nativeQuery(query);
        assertThat(resultObject, is(instanceOf(List.class)));

        List<E> resultList = (List<E>) resultObject;
        assertEquals(1, resultList.size());
        assertEquals(2L, (long) resultList.get(0).getId());
    }

    @Test(expected = PersistenceException.class)
    public void nativeQuery_qlString_invalidSyntax_throwsPersistenceException() throws Exception {
        repository.nativeQuery("INVALID SYNTAX");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void nativeQuery_withUnknownQueryObject_throwsException() throws Exception {
        repository.nativeQuery(new Object());
    }

    public R getRepository() {
        return repository;
    }

    protected abstract R newRepository();
}
