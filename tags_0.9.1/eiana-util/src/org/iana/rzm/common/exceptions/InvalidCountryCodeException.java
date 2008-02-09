package org.iana.rzm.common.exceptions;

/**
 * @author Jakub Laszkiewicz
 */
public class InvalidCountryCodeException extends RuntimeException {
    String cc;

    public InvalidCountryCodeException(String cc) {
        this.cc = cc;
    }
}
