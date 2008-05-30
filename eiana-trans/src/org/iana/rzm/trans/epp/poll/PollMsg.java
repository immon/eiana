package org.iana.rzm.trans.epp.poll;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.Transaction;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class PollMsg {

    @Id
    @GeneratedValue
    private Long id;

    @Basic
    private long transactionID;

    @Basic
    private long ticketID;

    @Basic
    private String eppID;

    @Basic
    private String name;

    @Basic
    private String status;

    @Basic
    private String message;

    private boolean isread;

    @Basic
    private Timestamp created;

    public PollMsg(Transaction transaction, String status, String message) {
        CheckTool.checkNull(transaction, "transaction");
        this.transactionID = transaction.getTransactionID();
        this.ticketID = transaction.getTicketID();
        this.eppID = transaction.getEppRequestId();
        this.name = transaction.getCurrentDomain().getName();
        this.status = status;
        this.message = message;
        this.isread = false;
        this.created = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
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

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isread;
    }

    public void setRead(boolean read) {
        this.isread = read;
    }

    public Timestamp getCreated() {
        return created;
    }
}
