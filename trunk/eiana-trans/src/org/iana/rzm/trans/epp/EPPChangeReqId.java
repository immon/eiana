package org.iana.rzm.trans.epp;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPChangeReqId implements EPPIdGenerator {

    private Transaction transaction;

    public EPPChangeReqId(Transaction transaction) {
        CheckTool.checkNull(transaction, "transaction");
        this.transaction = transaction;
    }

    public String id() {
        String ret = "" + transaction.getTicketID();
        int retries = transaction.getEPPRetries();
        return ret + ":" + retries;
    }

    public static long extractTicketID(String id) {
        CheckTool.checkNull(id, "change request id");
        String[] parts = id.split(":");
        if (parts == null || parts.length == 0) {
            throw new IllegalStateException("invalid change request id: cannot split by ':' [" + id + "]");
        }
        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("invalid change request id: not a number [" + parts[0] +"]");
        }
    }
}
