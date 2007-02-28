package org.iana.rzm.trans.change;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Entity;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
abstract public class AbstractValue<T extends Change> implements Value<T> {
    private Long objId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }
}
