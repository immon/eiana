package org.iana.rzm.common.exceptions;

/**
 * @author Jakub Laszkiewicz
 */
public class InvalidEmailException extends RuntimeException {
    String email;

    public InvalidEmailException(String email) {
        this.email = email;
    }
}
