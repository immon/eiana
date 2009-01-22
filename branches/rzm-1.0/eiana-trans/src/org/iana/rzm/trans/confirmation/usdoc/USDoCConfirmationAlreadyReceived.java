package org.iana.rzm.trans.confirmation.usdoc;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCConfirmationAlreadyReceived extends USDoCConfirmationException {

    public USDoCConfirmationAlreadyReceived(boolean databaseChangeConfirmation) {
        super(databaseChangeConfirmation);
    }
}
