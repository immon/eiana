package org.iana.codevalues;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This class represents a single value associated with a given code. It consists
 * of unique identifier and readable valueName.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class Value implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String valueId;
    @Basic
    private String valueName;

    private Value() {
    }

    public Value(String id, String name) {
        if (id == null) throw new IllegalArgumentException("null valueId");
        if (name == null) throw new IllegalArgumentException("null valueName");
        
        this.valueId = id;
        this.valueName = name;
    }

    public String getValueId() {
        return valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value = (Value) o;

        if (objId != null ? !objId.equals(value.objId) : value.objId != null) return false;
        if (valueName != null ? !valueName.equals(value.valueName) : value.valueName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (objId != null ? objId.hashCode() : 0);
        result = 31 * result + (valueName != null ? valueName.hashCode() : 0);
        return result;
    }
}
