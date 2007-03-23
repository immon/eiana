package org.iana.rzm.facade.system.trans;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionActionVO {

    public static enum Name {
        CREATE_NEW_TLD,
        MODIFY_SUPPORTING_ORGANIZATION,
        MODIFY_CONTACT,
        MODIFY_NAMESERVER,
        MODIFY_REGISTRATION_URL,
        MODIFY_WHOIS_SERVER
    }

    private Name name;
    private List<ChangeVO> change;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = Name.valueOf(name);
    }
    
    public List<ChangeVO> getChange() {
        return change;
    }

    public void setChange(List<ChangeVO> change) {
        this.change = change;
    }

    public void addChange(ChangeVO change) {
        if (this.change == null) this.change = new ArrayList<ChangeVO>();
        this.change.add(change);
    }
}
