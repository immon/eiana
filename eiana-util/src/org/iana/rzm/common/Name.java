package org.iana.rzm.common;

import org.iana.dns.validator.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class Name implements Cloneable, Serializable {

    @Basic
    private String name;

    protected Name() {}

    public Name(String name) throws InvalidDomainNameException {
        if (name == null) throw new NullPointerException("name is null");
        this.name = DomainNameValidator.validateName(name);
    }

    final public String getName() {
        return name;
    }

    public String getFqdnName() {
        return name.startsWith(".") ? name.toUpperCase(Locale.ENGLISH) : "." + name.toUpperCase(Locale.ENGLISH);
    }

    public String getNameWithDot() {
        return (name != null && !name.endsWith("."))? name + "." : name;    
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name1 = (Name) o;

        if (name != null ? !name.equals(name1.name) : name1.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    public Object clone() throws CloneNotSupportedException {
        Name newName = (Name) super.clone();
        newName.name = name;
        return newName;
    }
}
