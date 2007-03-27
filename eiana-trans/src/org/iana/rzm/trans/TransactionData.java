package org.iana.rzm.trans;

import org.hibernate.annotations.MapKeyManyToMany;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransactionData_stateConfirmations",
            inverseJoinColumns = @JoinColumn(name = "StateConfirmation_objId"))
    @MapKeyManyToMany
    private Map<String, StateConfirmations> stateConfirmations = new HashMap<String, StateConfirmations>();

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

    public Confirmation getStateConfirmations(String name) {
        return stateConfirmations.get(name);
    }

    public void setStateConfirmations(String state, StateConfirmations stateConfirmations) {
        this.stateConfirmations.put(state, stateConfirmations);
    }
}
