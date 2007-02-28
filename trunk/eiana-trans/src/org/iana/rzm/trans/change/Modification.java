package org.iana.rzm.trans.change;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Modification extends Change {

    private Value<? extends Change> value;

    private Modification() {}

    public Modification(String fieldName, Value<? extends Change> value) {
        super(fieldName, Type.UPDATE);
        this.value = value;
    }

    @ManyToOne(cascade = CascadeType.ALL,
            targetEntity = AbstractValue.class)
    @JoinColumn(name = "modificationValue_objId")
    public Value<? extends Change> getValue() {
        return value;
    }

    public void setValue(Value<? extends Change> value) {
        this.value = value;
    }
}
