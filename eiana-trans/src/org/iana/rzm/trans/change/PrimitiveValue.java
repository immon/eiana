package org.iana.rzm.trans.change;

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
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
