package org.iana.rzm.facade.auth;

/**
 * org.iana.rzm.facade.auth.AuthenticationInternalException
 *
 * @author Marcin Zajaczkowski
 *
 * @deprecated Not needed after refactoring
 */
public class AuthenticationInternalException extends AuthenticationException {

    public AuthenticationInternalException() {
        super();
    }

    public AuthenticationInternalException(String message) {
        super(message);
    }

    public AuthenticationInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationInternalException(Throwable cause) {
        super(cause);
    }
}
