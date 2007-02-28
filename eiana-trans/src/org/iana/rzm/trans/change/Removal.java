package org.iana.rzm.trans.change;

import org.iana.rzm.trans.change.Change;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Removal extends AdditionOrRemoval {

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

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AbstractValue.class)
    @JoinColumn(name = "removalValue_objId")
    public Value<Removal> getValue() {
        return value;
    }

    public void setValue(Value<Removal> value) {
        this.value = value;
    }
}
