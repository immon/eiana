package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

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

    public void accept(ValueVisitor visitor) {
        CheckTool.checkNull(visitor, "value visitor");
        visitor.visitModifiedPrimitiveValue(this);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModifiedPrimitiveValue that = (ModifiedPrimitiveValue) o;

        if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
        if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (newValue != null ? newValue.hashCode() : 0);
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        return result;
    }
}
