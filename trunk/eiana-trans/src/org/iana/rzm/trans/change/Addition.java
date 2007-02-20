package org.iana.rzm.trans.change;

public class Addition extends AdditionOrRemoval {

    private Value<Addition> value;

    public Addition(String fieldName, String value) {
        this(fieldName, new PrimitiveValue<Addition>(value));
    }

    public Addition(String fieldName, Value<Addition> value) {
        super(fieldName, Type.ADD);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
