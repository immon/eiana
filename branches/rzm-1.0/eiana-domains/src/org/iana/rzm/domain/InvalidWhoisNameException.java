package org.iana.rzm.domain;

/**
 * Thrown when a whois name is invalid i.e. it's not a domain name nor a valid IP address.
 *
 * @author Patrycja Wegrzynowicz
 */
public class InvalidWhoisNameException extends RuntimeException {

    private String name;

    public InvalidWhoisNameException(String name) {
        super("the whois name must be a valid domain name or valid IP address");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
