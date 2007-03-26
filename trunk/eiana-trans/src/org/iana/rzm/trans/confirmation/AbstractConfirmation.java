package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;

import javax.persistence.*;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(name = "Confirmation")
public abstract class AbstractConfirmation implements Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String name;
    @Enumerated
    private SystemRole.SystemType type;

    protected AbstractConfirmation() {}

    protected AbstractConfirmation(String name, SystemRole.SystemType type) {
        this.name = name;
        this.type = type;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getName() {
        return name;
    }

    public SystemRole.SystemType getType() {
        return type;
    }
}
