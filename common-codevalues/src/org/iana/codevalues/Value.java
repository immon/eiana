package org.iana.codevalues;

import javax.persistence.Entity;

/**
 * This class represents a single value associated with a given code. It consists
 * of unique identifier and readable name.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class Value {

    private String id;
    private String name;

    public Value(String id, String name) {
        if (this.id == null) throw new IllegalArgumentException("null id");
        if (this.name == null) throw new IllegalArgumentException("null name");
        
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
