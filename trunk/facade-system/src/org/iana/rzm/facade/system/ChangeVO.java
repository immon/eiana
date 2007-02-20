package org.iana.rzm.facade.system;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ChangeVO<T extends ValueVO> {

    public enum Type { ADD, REMOVE, UPDATE }

    private String fieldName;
    private Type type;
    private T value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
