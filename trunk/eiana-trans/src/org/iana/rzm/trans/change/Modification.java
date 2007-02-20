package org.iana.rzm.trans.change;

public class Modification extends Change {

    private Value<? extends Change> value;

    public Modification(String fieldName, Value<? extends Change> value) {
        super(fieldName, Type.UPDATE);
        this.value = value;
    }

    public Value<? extends Change> getValue() {
        return value;
    }

    public void setValue(Value<? extends Change> value) {
        this.value = value;
    }
}
