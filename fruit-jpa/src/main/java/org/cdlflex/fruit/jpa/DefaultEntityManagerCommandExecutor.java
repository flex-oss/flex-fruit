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

/**
 * DefaultEntityManagerCommandExecutor that does not initialize a EntityTransaction, but passes a null reference to the
 * EntityManagerCommand.
 * 
 * FIXME: this is bad design
 */
public class DefaultEntityManagerCommandExecutor implements EntityManagerCommandExecutor {

    private EntityManager entityManager;

    public DefaultEntityManagerCommandExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void execute(EntityManagerCommand command) {
        try {
            command.execute(entityManager, null);
            entityManager.flush();
            command.onAfterCommit(entityManager, null);
        } catch (Exception e) {
            command.onException(entityManager, null, e);
        }
    }
}
