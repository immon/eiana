package org.iana.rzm.trans.confirmation.usdoc;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCConfirmationMismatch extends USDoCConfirmationException {

    public USDoCConfirmationMismatch(boolean databaseChangeConfirmation) {
        super(databaseChangeConfirmation);
    }
}
