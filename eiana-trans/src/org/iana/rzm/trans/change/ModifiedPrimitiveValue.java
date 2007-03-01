package org.iana.rzm.trans.change;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Basic;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class ModifiedPrimitiveValue extends AbstractValue<Modification> implements Value<Modification> {

    @Basic
    @Column(name = "modifiedPrimitiveNewValue")
    String newValue;
    @Basic
    @Column(name = "modifiedPrimitiveOldValue")
    String oldValue;

    public ModifiedPrimitiveValue() {
    }

    public ModifiedPrimitiveValue(String newValue, String oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
