package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PasswordExpiredException extends AuthenticationFailedException {

    public PasswordExpiredException() {
    }

    public PasswordExpiredException(String message) {
        super(message);
    }

    public PasswordExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordExpiredException(Throwable cause) {
        super(cause);
    }

}
