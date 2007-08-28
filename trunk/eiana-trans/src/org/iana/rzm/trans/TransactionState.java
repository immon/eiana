package org.iana.rzm.trans;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class TransactionState {

    public static enum Name {
        PENDING_CREATION,
        PENDING_TECH_CHECK,
        PENDING_TECH_CHECK_REMEDY,
        PENDING_CONTACT_CONFIRMATION,
        PENDING_SOENDORSEMENT,
        PENDING_IMPACTED_PARTIES,
        PENDING_MANUAL_REVIEW,
        PENDING_EXT_APPROVAL,
        PENDING_EVALUATION,
        PENDING_IANA_CHECK,
        PENDING_SUPP_TECH_CHECK,
        PENDING_SUPP_TECH_CHECK_REMEDY,
        PENDING_USDOC_APPROVAL,
        PENDING_ZONE_INSERTION,
        PENDING_ZONE_PUBLICATION,
        PENDING_DATABASE_INSERTION,
        COMPLETED,
        WITHDRAWN,
        REJECTED,
        ADMIN_CLOSED,
        EXCEPTION,
        PENDING_IANA_CONFIRMATION; //todo for back compatibility only
        public static final List<String> nameStrings = new ArrayList<String>();

        static {
            for (Name name : Name.values())
                nameStrings.add(name.toString());
        }
    }

    @Transient
    private Long objId;
    @Enumerated(javax.persistence.EnumType.STRING)
    private Name name;
    @Basic
    private Timestamp start;
    @Basic
    private Timestamp end;
    @Transient
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
        try {
            this.name = Name.valueOf(name);
        } catch (IllegalArgumentException e) {
            //TODO replace with log4j
            System.out.println("Wrong state name!:" + name);
            throw e;
        }

    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setStart(Date start) {
        if (start != null) {
            this.start = new Timestamp(start.getTime());
            this.start.setNanos(0);
        } else
            this.start = null;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setEnd(Date end) {
        if (end != null) {
            this.end = new Timestamp(end.getTime());
            this.end.setNanos(0);
        } else
            this.end = null;
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
