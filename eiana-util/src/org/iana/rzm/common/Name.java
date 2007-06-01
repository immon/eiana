package org.iana.rzm.common;

import org.iana.dns.validator.DomainNameValidator;
import org.iana.dns.validator.InvalidDomainNameException;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;

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

    private void isValidName(String name) throws InvalidDomainNameException {
        DomainNameValidator.validateName(name);
    }

    public Object clone() throws CloneNotSupportedException {
        Name newName = (Name) super.clone();
        newName.name = name;
        return newName;
    }
}
