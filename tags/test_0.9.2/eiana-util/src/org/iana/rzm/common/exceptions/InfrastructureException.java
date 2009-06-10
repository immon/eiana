package org.iana.rzm.common.exceptions;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InfrastructureException extends Exception {
    public InfrastructureException() {
    }

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfrastructureException(Throwable cause) {
        super(cause);
    }
}
