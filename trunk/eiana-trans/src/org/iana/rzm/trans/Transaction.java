package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.TrackData;
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
public class Transaction implements TrackedObject {

    private Long transactionID;
    private Long rtID;
    private String name;
    private Domain currentDomain;
    private List<Action> actions;
    private State state;
    private Timestamp start;
    private Timestamp end;
    private Long objId;
    private TrackData trackData = new TrackData();

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getRtID() {
        return rtID;
    }

    public void setRtID(Long rtID) {
        this.rtID = rtID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "currentDomain_objId")
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
    @JoinColumn(name = "state_objId")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    @Transient
    public Long getId() {
        return trackData.getId();
    }

    @Transient
    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    @Transient
    public Timestamp getModified() {
        return trackData.getModified();
    }

    @Transient
    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    @Transient
    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    @Embedded
    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (currentDomain != null ? !currentDomain.equals(that.currentDomain) : that.currentDomain != null)
            return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (rtID != null ? !rtID.equals(that.rtID) : that.rtID != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (trackData != null ? !trackData.equals(that.trackData) : that.trackData != null) return false;
        if (transactionID != null ? !transactionID.equals(that.transactionID) : that.transactionID != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (transactionID != null ? transactionID.hashCode() : 0);
        result = 31 * result + (rtID != null ? rtID.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (currentDomain != null ? currentDomain.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }
}
