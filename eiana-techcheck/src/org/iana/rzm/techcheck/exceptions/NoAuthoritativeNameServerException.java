package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class NoAuthoritativeNameServerException extends DomainCheckException {

    private String nameServerName;

    public NoAuthoritativeNameServerException(String nameServerName) {
        this.nameServerName = nameServerName;
    }

    public String getNameServerName() {
        return nameServerName;
    }
}
