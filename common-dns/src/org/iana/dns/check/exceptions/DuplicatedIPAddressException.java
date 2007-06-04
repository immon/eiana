package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author: Piotr Tkaczyk
 */
public class DuplicatedIPAddressException extends DNSTechnicalCheckException {
    private String nameServer;
    private String ipAddress;

    public DuplicatedIPAddressException(String nameServer, String ipAddress) {
        this.nameServer = nameServer;
        this.ipAddress = ipAddress;
    }

    public String getNameServer() {
        return nameServer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DuplicatedIPAddressException e = (DuplicatedIPAddressException) o;

        if (nameServer != null ? !nameServer.equals(e.nameServer) : e.nameServer != null) return false;
        if (ipAddress != null ? !ipAddress.equals(e.ipAddress) : e.ipAddress != null) return false;
        return true;
    }
}
