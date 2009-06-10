package org.iana.rzm.user;

import javax.persistence.*;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(name = "Password")
public abstract class AbstractPassword implements Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
