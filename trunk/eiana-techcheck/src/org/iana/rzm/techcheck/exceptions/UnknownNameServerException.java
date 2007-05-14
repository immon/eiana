package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class UnknownNameServerException extends DomainCheckException {

    private String nameServerName;

    public UnknownNameServerException(String nameServerName) {
        this.nameServerName = nameServerName;
    }

    public String getNameServerName() {
        return nameServerName;
    }
}
