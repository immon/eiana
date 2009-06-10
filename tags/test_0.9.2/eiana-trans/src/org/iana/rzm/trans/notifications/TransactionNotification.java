package org.iana.rzm.trans.notifications;

import org.iana.notifications.PNotification;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class TransactionNotification {
    @Id
    @GeneratedValue
    long id;

    @Basic
    long transactionID;

    @ManyToOne (cascade = {CascadeType.PERSIST})
    PNotification notification;

    protected TransactionNotification() {
    }

    public TransactionNotification(Transaction trans, PNotification notification) {
        CheckTool.checkNull(trans, "transaction");
        CheckTool.checkNull(notification, "notification");
        this.transactionID = trans.getTransactionID();
        this.notification = notification;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public PNotification getNotification() {
        return notification;
    }

}
