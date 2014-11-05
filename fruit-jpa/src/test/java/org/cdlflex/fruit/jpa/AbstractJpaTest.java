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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for tests that require an EntityManager that use the repository-test persistence context.
 */
public class AbstractJpaTest {

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    @Before
    public void setUpEntityManager() throws Exception {
        emf = Persistence.createEntityManagerFactory("repository-test");
        entityManager = emf.createEntityManager();
    }

    @After
    public void shutdownDataSource() throws Exception {
        getEntityManager().getTransaction().begin();
        getEntityManager().createNativeQuery("SHUTDOWN").executeUpdate();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EntityManager createNewEntityManager() {
        return emf.createEntityManager();
    }
}
