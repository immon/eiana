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
public class Addition extends AdditionOrRemoval {

    private Value<Addition> value;

    private Addition() {}

    public Addition(String fieldName, String value) {
        this(fieldName, new PrimitiveValue<Addition>(value));
    }

    public Addition(String fieldName, Value<Addition> value) {
        super(fieldName, Type.ADD);
        this.value = value;
    }

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AbstractValue.class)
    @JoinColumn(name = "additionValue_objId")
    public Value getValue() {
        return value;
    }

    private void setValue(Value<Addition> value) {
        this.value = value;
    }
}
