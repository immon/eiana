package org.iana.rzm.trans;

import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.AbstractValue;

import javax.persistence.*;
import java.util.List;
import java.util.Collections;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Action {

    public static enum Name {
        CREATE_NEW_TLD,
        MODIFY_SUPPORTING_ORGANIZATION,
        MODIFY_CONTACT,
        MODIFY_NAMESERVER,
        MODIFY_REGISTRATION_URL,
        MODIFY_WHOIS_SERVER
    }

    private Long objId;
    private Name name;
    private List<Change> change;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Action_Changes",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    public List<Change> getChange() {
        return Collections.unmodifiableList(change);
    }

    public void setChange(List<Change> change) {
        this.change = change;
    }
}
