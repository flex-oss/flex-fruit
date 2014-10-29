package org.cdlflex.fruit.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * A command interface that can be used to encapsulate an execution around a transaction.
 */
public interface EntityManagerCommand {

    /**
     * Executes the command. May throw a RuntimeException that causes the transaction to be rolled back.
     * 
     * @param em the entity manager providing the transaction
     * @param tx the transaction
     */
    void execute(EntityManager em, EntityTransaction tx);

    /**
     * Executed after {@code javax.persistence.EntityTransaction#commit()} was executed. May throw a RuntimeException
     * that causes the transaction to be rolled back.
     * 
     * @param em the entity manager providing the transaction
     * @param tx the transaction
     */
    void onAfterCommit(EntityManager em, EntityTransaction tx);

    /**
     * Executed when an exception was caught and the transaction has to be rolled back.
     * 
     * @param em the entity manager providing the transaction
     * @param tx the transaction
     * @param e the caught exception
     */
    void onException(EntityManager em, EntityTransaction tx, Exception e);
}
