package org.iana.rzm.facade.system.trans;

/**
 * It signals a failure of creation of a ticket in the ticketing system.
 *
 * @author Patrycja Wegrzynowicz
 */
public class CreateTicketException extends TransactionServiceException {

    public CreateTicketException(long transactionId) {
        super(transactionId);
    }
}
