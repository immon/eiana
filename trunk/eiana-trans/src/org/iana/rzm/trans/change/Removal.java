package org.iana.rzm.trans.change;

import org.iana.rzm.trans.change.Change;

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

    public Value getValue() {
        return value;
    }

    public void setValue(Value<Removal> value) {
        this.value = value;
    }
}
