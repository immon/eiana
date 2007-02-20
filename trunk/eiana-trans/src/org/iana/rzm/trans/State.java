package org.iana.rzm.trans;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class State {

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

    private Name name;
    private Timestamp start;
    private Timestamp end;
    private Set<Transition> availableTransitions;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
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

    public Set<Transition> getAvailableTransitions() {
        return availableTransitions;
    }

    public void setAvailableTransitions(Set<Transition> availableTransitions) {
        this.availableTransitions = availableTransitions;
    }
}
