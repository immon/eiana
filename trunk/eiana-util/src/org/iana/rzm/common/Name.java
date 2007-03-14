package org.iana.rzm.common;

import org.iana.rzm.common.exceptions.InvalidNameException;

import javax.persistence.Embeddable;
import javax.persistence.Basic;
import java.util.Locale;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class Name implements Cloneable{

    @Basic
    private String name;

    protected Name() {}

    public Name(String name) throws InvalidNameException {
        if (name == null) throw new NullPointerException("name is null");
        name = name.toLowerCase(Locale.ENGLISH);
        isValidName(name);
        this.name = name;
    }

    final public String getName() {
        return name;
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

    private static String DOMAIN_PATTERN = "([a-z0-9\\-]+\\.)*[a-z0-9\\-]+";

    private void isValidName(String name) throws InvalidNameException {
        if (name != null && !name.matches(DOMAIN_PATTERN)) throw new InvalidNameException(name);
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
