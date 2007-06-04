package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author: Piotr Tkaczyk
 */
public class SerialNumberNotEqualException extends DNSTechnicalCheckException {

    private String nameServer;
    private long serialNumber;

    public SerialNumberNotEqualException(String nameServer, long serialNumber) {
        this.nameServer = nameServer;
        this.serialNumber = serialNumber;
    }

    public String getNameServer() {
        return nameServer;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerialNumberNotEqualException e = (SerialNumberNotEqualException) o;

        if (nameServer != null ? !nameServer.equals(e.nameServer) : e.nameServer != null) return false;
        if (serialNumber != e.serialNumber) return false;
        return true;
    }
}
