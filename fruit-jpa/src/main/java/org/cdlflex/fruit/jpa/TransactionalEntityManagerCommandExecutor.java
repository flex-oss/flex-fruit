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
import javax.persistence.EntityTransaction;

/**
 * An EntityManagerCommandExecutor that manually wraps an EntityManagerCommand around an EntityTransaction.
 */
public class TransactionalEntityManagerCommandExecutor implements EntityManagerCommandExecutor {
    private final EntityManager em;

    public TransactionalEntityManagerCommandExecutor(EntityManager em) {
        this.em = em;
    }

    @Override
    public void execute(EntityManagerCommand command) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            command.execute(em, tx);
            tx.commit();
            command.onAfterCommit(em, tx);
            // CHECKSTYLE:OFF catching Exception is okay here, as it is rethrown
        } catch (Exception e) {
            // CHECKSTYLE:ON
            command.onException(em, tx, e);
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
