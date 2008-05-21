package org.iana.rzm.trans.epp.poll;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.Transaction;

import javax.persistence.Basic;
import javax.persistence.Entity;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class PollMsg {

    @Basic
    private Long id;

    @Basic
    private long transactionID;

    @Basic
    private String name;

    @Basic
    private String status;

    @Basic
    private String message;

    @Basic
    private boolean read;

    @Basic
    private Timestamp created;

    public PollMsg(Transaction transaction, String status, String message) {
        CheckTool.checkNull(transaction, "transaction");
        this.transactionID = transaction.getTransactionID();
        this.name = transaction.getCurrentDomain().getName();
        this.status = status;
        this.message = message;
        this.read = false;
        this.created = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public long getTransactionID() {
        return transactionID;
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
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreated() {
        return created;
    }
}
