package org.iana.rzm.domain;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.DomainNameValidator;
import org.iana.dns.validator.IPAddressValidator;
import org.iana.dns.validator.InvalidIPAddressException;

import javax.persistence.Embeddable;

/**
 * It represents a valid whois name. A valid whois name is a valid domain name or a valid IP address (both IPv4 or IPv6).
 *
 * @author Patrycja Wegrzynowicz
 */
@Embeddable
public class WhoisName implements Cloneable {

    private String name;

    // for hibernate
    protected WhoisName() {
    }

    public WhoisName(String name) throws InvalidWhoisNameException {
        setName(name);
    }

    public void setName(String name) throws InvalidWhoisNameException {
        if (name == null) throw new IllegalArgumentException("null name");
        try {
            name = DomainNameValidator.validateName(name);
        } catch (InvalidDomainNameException e) {
            try {
                // if not a domain name, then maybe IP address
                IPAddressValidator.getInstance().validate(name);
            } catch (InvalidIPAddressException e1) {
                // if neither a domain name nor IP address... then exception
                throw new InvalidWhoisNameException(name);
            }
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNameWithDot() {
        return name + ".";
    }

    public String getNameWithDots() {
        if (name.length() == 0) return ".";
        return "." + name + ".";
    }

    public String[] getLabels() {
        return name.split("\\.");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhoisName name1 = (WhoisName) o;

        if (name != null ? !name.equals(name1.name) : name1.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    public WhoisName clone() {
        try {
            return (WhoisName) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
