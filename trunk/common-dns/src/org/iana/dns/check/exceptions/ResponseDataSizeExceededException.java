package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author: Piotr Tkaczyk
 */
public class ResponseDataSizeExceededException extends DNSTechnicalCheckException {
    private String nameServer;

    public ResponseDataSizeExceededException(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getNameServer() {
        return nameServer;
    }
}
