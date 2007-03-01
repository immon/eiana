package org.iana.rzm.trans.change;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Removal extends AdditionOrRemoval {

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AbstractValue.class)
    @JoinColumn(name = "removalValue_objId")
    private Value<Removal> value;

    public Removal() {
        this(null, (Value<Removal>) null);
    }

    public Removal(String fieldName, String value) {
        this(fieldName, new PrimitiveValue(value));
    }

    public Removal(String fieldName, Value<Removal> value) {
        super(fieldName, Type.REMOVE);
        this.value = value;
    }

    public Value<Removal> getValue() {
        return value;
    }

    public void setValue(Value<Removal> value) {
        this.value = value;
    }
}
