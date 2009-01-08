package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.facade.auth.AuthenticationFailedException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDException extends AuthenticationFailedException {

    public SecurIDException() {
    }

    public SecurIDException(String message) {
        super(message);
    }

    public SecurIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurIDException(Throwable cause) {
        super(cause);
    }
}
