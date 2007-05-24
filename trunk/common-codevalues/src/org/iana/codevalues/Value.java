package org.iana.codevalues;

import javax.persistence.*;

/**
 * This class represents a single value associated with a given code. It consists
 * of unique identifier and readable name.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String id;
    @Basic
    private String name;

    public Value(String id, String name) {
        if (id == null) throw new IllegalArgumentException("null id");
        if (name == null) throw new IllegalArgumentException("null name");
        
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
