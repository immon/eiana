package org.iana.dns.obj;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.DomainNameValidator;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Name {

    private String name;

    public Name(String name) throws InvalidDomainNameException {
        setName(name);
    }

    public void setName(String name) throws InvalidDomainNameException {
        if (name == null) throw new IllegalArgumentException("null name");
        name = name.toLowerCase();
        DomainNameValidator.validateName(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNameWithDot() {
        return name + ".";
    }

    public String[] getLabels() {
        return name.split("\\.");
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
}
