package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ModifiedPrimitiveValue implements Value<Modification> {

    String newValue;
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
