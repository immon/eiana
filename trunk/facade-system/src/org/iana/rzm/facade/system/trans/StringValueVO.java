package org.iana.rzm.facade.system.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StringValueVO extends ValueVO {

    /**
     * Stores a current value for a given field if associated with ChangeVO of type REMOVE, UPDATE.
     */
    private String oldValue;
    /**
     * Stores a new value for a given field if associated with ChangeVO of type ADD, UPDATE.
     */
    private String newValue;

    public StringValueVO(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
