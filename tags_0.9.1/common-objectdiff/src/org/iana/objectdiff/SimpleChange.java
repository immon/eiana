package org.iana.objectdiff;

import javax.persistence.Entity;
import javax.persistence.Basic;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class SimpleChange extends Change {

    @Basic
    private String oldValue;
    @Basic
    private String newValue;

    private SimpleChange() {}

    public SimpleChange(Object oldValue, Object newValue) {
        super(determineType(oldValue, newValue));
        this.oldValue = oldValue == null ? null : oldValue.toString();
        this.newValue = newValue == null ? null : newValue.toString();
    }

    private static Type determineType(Object oldValue, Object newValue) {
        if (oldValue != null && newValue != null) return Type.UPDATE;
        return newValue != null ? Type.ADDITION : Type.REMOVAL;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void accept(ChangeVisitor v) {
        v.visitSimple(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleChange that = (SimpleChange) o;

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
