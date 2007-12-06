package org.iana.rzm.trans.confirmation.usdoc;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCConfirmationNotExpected extends USDoCConfirmationException {

    public USDoCConfirmationNotExpected(boolean databaseChangeConfirmation) {
        super(databaseChangeConfirmation);
    }
    
}
