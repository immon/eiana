package org.iana.rzm.facade.system.trans;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionStateVO implements Serializable {

    public static enum Name {
        FIRST_NSLINK_CHANGE_DECISION,
        PENDING_TECH_CHECK,
        PENDING_TECH_CHECK_REMEDY,
        PENDING_CONTACT_CONFIRMATION,
        MODIFICATIONS_IN_CONTACT_DECISION,
        PENDING_SOENDORSEMENT,
        NS_SHARED_GLUE_CHANGE_DECISION,
        PENDING_IMPACTED_PARTIES,
        PENDING_MANUAL_REVIEW,
        MATCHES_SI_BREAKPOINT_DECISION,
        PENDING_EXT_APPROVAL,
        REDEL_FLAG_SET_DECISION,
        PENDING_EVALUATION,
        PENDING_IANA_CHECK,
        SECOND_NSLINK_CHANGE_DECISION,
        PENDING_SUPP_TECH_CHECK,
        PENDING_SUPP_TECH_CHECK_REMEDY,
        PENDING_USDOC_APPROVAL,
        NS_CHANGE_DECISION,
        PENDING_ZONE_INSERTION,
        PENDING_ZONE_PUBLICATION,
        PENDING_DATABASE_INSERTION,
        COMPLETED,
        WITHDRAWN,
        REJECTED,
        ADMIN_CLOSED,
        EXCEPTION,
        PENDING_IANA_CONFIRMATION  //todo for back compatibility only
    }

    private Name name;
    private Timestamp start;
    private Timestamp end;

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

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionStateVO that = (TransactionStateVO) o;

        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (name != that.name) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
