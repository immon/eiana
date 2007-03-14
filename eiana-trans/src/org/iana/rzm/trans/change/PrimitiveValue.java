package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Basic;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(name = "Value")
public class PrimitiveValue<T extends AdditionOrRemoval> extends AbstractValue<T> implements Value<T> {

    @Basic
    @Column(name = "primitiveValue")
    private String value;

    public PrimitiveValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void accept(ValueVisitor visitor) {
        CheckTool.checkNull(visitor, "value visitor");
        visitor.visitPrimitiveValue(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitiveValue that = (PrimitiveValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }
}
