package org.iana.rzm.facade.common.cc;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CountryCodeAlreadyExistsException extends Exception {
    public CountryCodeAlreadyExistsException(String message) {
        super(message);
    }
}
