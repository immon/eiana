package org.iana.dns.check.remote.client;

/**
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckClientException extends Exception {
    public TechnicalCheckClientException() {
    }

    public TechnicalCheckClientException(String message) {
        super(message);
    }

    public TechnicalCheckClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalCheckClientException(Throwable cause) {
        super(cause);
    }
}
