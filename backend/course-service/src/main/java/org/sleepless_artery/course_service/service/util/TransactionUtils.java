package org.sleepless_artery.course_service.service.util;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Utility for executing actions after successful transaction commit.
 */
public final class TransactionUtils {

    private TransactionUtils() {}

    /**
     * Executes the given action after the current transaction successfully commits.
     * If no transaction is active, executes immediately.
     *
     * @param action action to execute
     */
    public static void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        action.run();
                    }
                }
        );
    }
}