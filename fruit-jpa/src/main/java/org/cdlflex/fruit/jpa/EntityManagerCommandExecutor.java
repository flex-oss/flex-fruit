package org.cdlflex.fruit.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Executes EntityManagerCommand instances on a given EntityManager in a transactional way.
 */
public class EntityManagerCommandExecutor {

    private final EntityManager em;

    public EntityManagerCommandExecutor(EntityManager em) {
        this.em = em;
    }

    /**
     * Execute the given command around a transaction.
     * 
     * @param command the command to execute.
     */
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
