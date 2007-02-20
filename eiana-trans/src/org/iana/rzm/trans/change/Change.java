package org.iana.rzm.trans.change;

abstract public class Change {

    enum Type { ADD, REMOVE, UPDATE }
    
    private String fieldName;
    private Type type;

    protected Change(String fieldName, Type type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Type getType() {
        return type;
    }
}
