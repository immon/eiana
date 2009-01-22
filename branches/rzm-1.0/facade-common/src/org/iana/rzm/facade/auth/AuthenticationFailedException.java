package org.iana.rzm.facade.auth;

/**
 * org.iana.rzm.facade.auth
 *
 * @author Marcin Zajaczkowski
 */
public class AuthenticationFailedException extends AuthenticationException {

    public AuthenticationFailedException() {
        super();
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }
}
