package org.iana.rzm.trans.confirmation;

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

    protected AbstractConfirmation() {}

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }
}
