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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringValueVO that = (StringValueVO) o;

        if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
        if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }
}
