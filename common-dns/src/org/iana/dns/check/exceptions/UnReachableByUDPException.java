package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author: Piotr Tkaczyk
 */
public class UnReachableByUDPException extends DNSTechnicalCheckException {

    private String nameServer;

    public UnReachableByUDPException(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getNameServer() {
        return nameServer;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnReachableByUDPException e = (UnReachableByUDPException) o;

        if (nameServer != null ? !nameServer.equals(e.nameServer) : e.nameServer != null) return false;
        return true;
    }
}
