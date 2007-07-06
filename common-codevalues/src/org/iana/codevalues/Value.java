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
}
