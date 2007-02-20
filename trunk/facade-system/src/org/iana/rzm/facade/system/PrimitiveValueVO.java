package org.iana.rzm.facade.system;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PrimitiveValueVO extends ValueVO {

    /**
     * It stores a current value for a given field if associated with ChangeVO of type REMOVE, UPDATE.
     * It may be one of the following type: Number, String or Timestamp.
     */
    private Object oldValue;
    /**
     * It stores a new value for a given field if associated with ChangeVO of type ADD, UPDATE.
     * It may be one of the following type: Number, String or Timestamp.
     */
    private Object newValue;

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
