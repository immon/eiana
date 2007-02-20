package org.iana.rzm.trans;

import org.iana.rzm.trans.change.Change;

import java.util.List;
import java.util.Collections;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Action {

    public static enum Name {
        CREATE_NEW_TLD,
        MODIFY_SUPPORTING_ORGANIZATION,
        MODIFY_CONTACT,
        MODIFY_NAMESERVER,
        MODIFY_REGISTRATION_URL,
        MODIFY_WHOIS_SERVER
    }

    private Name name;
    private List<Change> change;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<Change> getChange() {
        return Collections.unmodifiableList(change);
    }

    public void setChange(List<Change> change) {
        this.change = change;
    }
}
