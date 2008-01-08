package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;

/**
 * @author Jakub Laszkiewicz
 */
public interface TestTransactionManager extends TransactionManager {
    public Transaction createTransactionTestTransaction(Domain domain);
    public Transaction createConfirmationTestTransaction(Domain domain);
}
