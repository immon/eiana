package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransactionData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private Long ticketID;
    @ManyToOne
    @JoinColumn(name = "currentDomain_objId")
    private Domain currentDomain;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domainChange_objId")
    private ObjectChange domainChange;
    @Transient
    private Map<String, StateConfirmations> stateConfirmationsMap;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransactionData_stateConfirmations",
            inverseJoinColumns = @JoinColumn(name = "StateConfirmations_objId"))
    private Set<StateConfirmations> stateConfirmations = new HashSet<StateConfirmations>();

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public Domain getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Domain currentDomain) {
        this.currentDomain = currentDomain;
    }

    public ObjectChange getDomainChange() {
        return domainChange;
    }

    public void setDomainChange(ObjectChange domainChange) {
        this.domainChange = domainChange;
    }

    private Map<String, StateConfirmations> getStateConfirmationsMap() {
        if (stateConfirmationsMap == null) {
            stateConfirmationsMap = new HashMap<String, StateConfirmations>();
            for (StateConfirmations sc : stateConfirmations)
                stateConfirmationsMap.put(sc.getState(), sc);
        }
        return stateConfirmationsMap;
    }

    public Confirmation getStateConfirmations(String name) {
        return getStateConfirmationsMap().get(name);
    }

    public void setStateConfirmations(StateConfirmations stateConfirmations) {
        this.stateConfirmations.add(stateConfirmations);
        getStateConfirmationsMap().put(stateConfirmations.getState(), stateConfirmations);
    }
}
