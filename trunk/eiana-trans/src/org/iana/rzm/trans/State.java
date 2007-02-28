package org.iana.rzm.trans;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
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

    private Long objId;
    private Name name;
    private Timestamp start;
    private Timestamp end;
    private Set<Transition> availableTransitions;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "State_AvailableTransitions",
            inverseJoinColumns = @JoinColumn(name = "Transition_objId"))
    public Set<Transition> getAvailableTransitions() {
        return availableTransitions;
    }

    public void setAvailableTransitions(Set<Transition> availableTransitions) {
        this.availableTransitions = availableTransitions;
    }
}
