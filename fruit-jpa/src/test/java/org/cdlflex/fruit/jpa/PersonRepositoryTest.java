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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.cdlflex.fruit.Filter;
import org.cdlflex.fruit.Operator;
import org.cdlflex.fruit.Predicate;
import org.cdlflex.fruit.jpa.model.Contact;
import org.cdlflex.fruit.jpa.model.Person;
import org.junit.Test;

public class PersonRepositoryTest extends GenericJpaRepositoryTest<Person, JpaRepository<Person>> {
    @Override
    protected JpaRepository<Person> newRepository() {
        return new JpaRepository<>(Person.class);
    }

    @Test
    public void findByForeignAttribute_returnsCorrectResult() throws Exception {
        Person jack = new Person("Jack");
        jack.setContact(new Contact("admin@example.com", "12345"));

        Person jill = new Person("Jill");
        jill.setContact(new Contact("admin@example.com", "23456"));

        Person adam = new Person("Adam");
        adam.setContact(new Contact("adam@example.com", "34567"));

        getRepository().save(Arrays.asList(jack, jill, adam));

        List<Person> result =
            getRepository().find(new Filter(new Predicate("contact.email", Operator.LIKE, "admin@%")));

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(jack, jill));
    }

}
