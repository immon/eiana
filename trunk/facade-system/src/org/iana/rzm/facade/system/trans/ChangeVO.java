package org.iana.rzm.facade.system.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ChangeVO {

    public enum Type { ADD, REMOVE, UPDATE }

    private String fieldName;
    private Type type;
    private ValueVO value;

    public String getFieldName() {
        return fieldName;
    }

    void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    void setType(Type type) {
        this.type = type;
    }

    public ValueVO getValue() {
        return value;
    }

    void setValue(ValueVO value) {
        this.value = value;
    }
}
