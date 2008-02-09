package org.iana.rzm.trans;

import org.iana.objectdiff.Change;

import javax.persistence.*;
import java.util.List;
import java.util.Collections;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransactionAction {

    public static enum Name {
        CREATE_NEW_TLD,
        MODIFY_SUPPORTING_ORGANIZATION,
        MODIFY_CONTACT,
        MODIFY_NAMESERVER,
        MODIFY_REGISTRATION_URL,
        MODIFY_WHOIS_SERVER
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Enumerated
    private Name name;
/*
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Action_Changes",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
*/
    @Transient
    private List<Change> change;

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

    public List<Change> getChange() {
        return Collections.unmodifiableList(change);
    }

    public void setChange(List<Change> change) {
        this.change = change;
    }
}
