package org.iana.rzm.trans.confirmation.usdoc;

import org.iana.rzm.trans.TransactionException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCConfirmationException extends TransactionException {

    boolean databaseChangeConfirmation;

    public USDoCConfirmationException(boolean databaseChangeConfirmation) {
        this.databaseChangeConfirmation = databaseChangeConfirmation;
    }

}
