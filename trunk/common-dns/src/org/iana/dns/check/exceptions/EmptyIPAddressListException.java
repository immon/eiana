package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author: Piotr Tkaczyk
 */

public class EmptyIPAddressListException extends DNSTechnicalCheckException {

    private String nameServer;

    public EmptyIPAddressListException(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getNameServer() {
        return nameServer;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyIPAddressListException e = (EmptyIPAddressListException) o;

        if (nameServer != null ? !nameServer.equals(e.nameServer) : e.nameServer != null) return false;
        return true;
    }
}
