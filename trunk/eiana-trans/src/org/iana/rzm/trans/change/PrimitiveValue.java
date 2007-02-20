package org.iana.rzm.trans.change;

public class PrimitiveValue<T extends AdditionOrRemoval> implements Value<T> {

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
