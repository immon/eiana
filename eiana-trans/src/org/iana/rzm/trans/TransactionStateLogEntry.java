package org.iana.rzm.trans;

import javax.persistence.*;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
@Table (name = "TransactionStateLog")
public class TransactionStateLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TransactionState state;
    @Basic
    private String userName;

    public TransactionStateLogEntry() {
    }

    public TransactionStateLogEntry(TransactionState state, String userName) {
        this.state = state;
        this.userName = userName;
    }

    public TransactionState getState() {
        return state;
    }

    public String getUserName() {
        return userName;
    }
}
