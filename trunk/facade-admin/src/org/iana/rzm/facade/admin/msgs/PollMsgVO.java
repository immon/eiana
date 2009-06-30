package org.iana.rzm.facade.admin.msgs;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PollMsgVO implements Serializable {

    private long id;

    private long transactionID;

    private long ticketID;

    private String eppID;

    private String domainName;

    private String status;

    private String message;

    private boolean read;

    private Timestamp created;

    public PollMsgVO(long id, long transactionID, long ticketID, String eppID, String domainName, String status, String message, boolean read, Timestamp created) {
        this.id = id;
        this.transactionID = transactionID;
        this.ticketID = ticketID;
        this.eppID = eppID;
        this.domainName = domainName;
        this.status = status;
        this.message = message;
        this.read = read;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public long getTicketID() {
        return ticketID;
    }

    public String getEppID() {
        return eppID;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Timestamp getCreated() {
        return created;
    }
}
