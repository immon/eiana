package org.iana.rzm.trans;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class TransactionState {

    public static enum Name {
        PENDING_CONTACT_CONFIRMATION,
        PENDING_IMPACTED_PARTIES,
        PENDING_IANA_CONFIRMATION,
        PENDING_EVALUATION,
        PENDING_EXT_APPROVAL,
        PENDING_TECH_CHECK,
        PENDING_TECH_CHECK_REMEDY,
        PENDING_USDOC_APPROVAL,
        PENDING_IANA_CHECK,
        PENDING_DATABASE_INSERTION,
        PENDING_ZONE_INSERTION,
        PENDING_ZONE_PUBLICATION,
        COMPLETED,
        WITHDRAWN,
        REJECTED,
        ADMIN_CLOSE,
        EXCEPTION
    }

    private Long objId;
    private Name name;
    private Timestamp start;
    private Timestamp end;
    private Set<StateTransition> availableTransitions;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = Name.valueOf(name);
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setStart(Date start) {
        this.start = new Timestamp(start.getTime());
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setEnd(Date end) {
        this.end = new Timestamp(end.getTime());
    }

    public Set<StateTransition> getAvailableTransitions() {
        return availableTransitions;
    }

    public void setAvailableTransitions(Set<StateTransition> availableTransitions) {
        this.availableTransitions = availableTransitions;
    }

    public void addAvailableTransition(StateTransition st) {
        if (availableTransitions == null)
            availableTransitions = new HashSet<StateTransition>();
        availableTransitions.add(st);
    }
}
