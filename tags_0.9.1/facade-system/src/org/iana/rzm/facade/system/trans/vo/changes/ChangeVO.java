package org.iana.rzm.facade.system.trans.vo.changes;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ChangeVO implements Serializable {

    public enum Type { ADDITION, REMOVAL, UPDATE } 

    private String fieldName;
    private Type type;
    private ValueVO value;

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

    public ValueVO getValue() {
        return value;
    }

    public void setValue(ValueVO value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeVO changeVO = (ChangeVO) o;

        if (fieldName != null ? !fieldName.equals(changeVO.fieldName) : changeVO.fieldName != null) return false;
        if (type != changeVO.type) return false;
        if (value != null ? !value.equals(changeVO.value) : changeVO.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (fieldName != null ? fieldName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
