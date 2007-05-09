package org.iana.rzm.trans;

import org.hibernate.annotations.MapKeyManyToMany;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.trans.confirmation.TransitionConfirmations;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            inverseJoinColumns = @JoinColumn(name = "stateConfirmations_objId"))
    @MapKeyManyToMany
    private Map<String, StateConfirmations> stateConfirmations = new HashMap<String, StateConfirmations>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransactionData_transitionConfirmations",
            inverseJoinColumns = @JoinColumn(name = "transitionConfirmations_objId"))
    @MapKeyManyToMany
    private Map<String, TransitionConfirmations> transitionConfirmations = new HashMap<String, TransitionConfirmations>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransactionData_transactionStateLog",
            inverseJoinColumns = @JoinColumn(name = "transactionStateLog_objId"))
    private List<TransactionStateLogEntry> stateLog = new ArrayList<TransactionStateLogEntry>();
    @Embedded
    protected TrackData trackData = new TrackData();

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

    public TransitionConfirmations getTransitionConfirmations(String stateName) {
        return transitionConfirmations.get(stateName);
    }

    public void addTransitionConfirmation(String stateName, String transitionName, Confirmation confirmation) {
        TransitionConfirmations tc = transitionConfirmations.get(stateName);
        if (tc == null) {
            tc = new TransitionConfirmations();
            this.transitionConfirmations.put(stateName, tc);
        }
        tc.addConfirmation(transitionName, confirmation);
    }

    public List<TransactionStateLogEntry> getStateLog() {
        return stateLog;
    }

    public void addStateLogEntry(TransactionStateLogEntry entry) {
        stateLog.add(entry);
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public TrackData getTrackData() {
        return trackData;
    }
}
