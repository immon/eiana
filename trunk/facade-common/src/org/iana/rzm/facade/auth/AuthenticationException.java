package org.iana.rzm.facade.auth;

/**
 * <p>A super exception of all thrown AuthenticationException.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticationException extends Exception {

    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
