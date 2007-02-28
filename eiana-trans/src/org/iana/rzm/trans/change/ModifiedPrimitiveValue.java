package org.iana.rzm.trans.change;

import javax.persistence.Entity;
import javax.persistence.Column;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class ModifiedPrimitiveValue extends AbstractValue<Modification> implements Value<Modification> {

    String newValue;
    String oldValue;

    public ModifiedPrimitiveValue() {
    }

    public ModifiedPrimitiveValue(String newValue, String oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Column(name = "modifiedPrimitiveNewValue")
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Column(name = "modifiedPrimitiveOldValue")
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
