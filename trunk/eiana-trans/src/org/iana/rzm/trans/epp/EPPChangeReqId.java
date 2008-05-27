package org.iana.rzm.trans.epp;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPChangeReqId implements EPPIdGenerator {

    private static final String SEPARATOR = ":";

    private long ticketID;

    private int retries;

    public EPPChangeReqId(Transaction transaction) {
        CheckTool.checkNull(transaction, "transaction");
        CheckTool.checkNull(transaction.getTicketID(), "ticketID");
        this.ticketID = transaction.getTicketID();
        this.retries = transaction.getEPPRetries();
    }

    public EPPChangeReqId(String eppID) {
        try {
            CheckTool.checkNull(eppID, "epp id");
            String[] parts = eppID.split(SEPARATOR);
            if (parts.length != 2) {
                throw new IllegalStateException(eppID + "is not a valid epp id");
            }
            ticketID = Long.parseLong(parts[0]);
            retries = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(eppID + "is not a valid epp id", e);
        }
    }

    public String id() {
        return String.valueOf(ticketID) + SEPARATOR + retries;
    }

    public long getTicketID() {
        return ticketID;
    }

    public int getRetries() {
        return retries;
    }
    
}
