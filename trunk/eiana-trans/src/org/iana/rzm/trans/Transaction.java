package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.trans.Action;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * This class represents a domain modification transaction.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Transaction extends TrackedObject {

    private Long transactionID;
    private Long rtID;
    private String name;
    private Domain currentDomain;
    private List<Action> actions;
    private State state;
    private Timestamp start;
    private Timestamp end;

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    @Column(name = "transactionRtID")
    public Long getRtID() {
        return rtID;
    }

    public void setRtID(Long rtID) {
        this.rtID = rtID;
    }

    @Column(name = "transactionName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "transactionCurrentDomain_objId")
    public Domain getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Domain currentDomain) {
        this.currentDomain = currentDomain;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Trasaction_Actions",
            inverseJoinColumns = @JoinColumn(name = "Action_objId"))
    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transactionState_objId")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Column(name = "transactionStart")
    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    @Column(name = "transactionEnd")
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }
}
