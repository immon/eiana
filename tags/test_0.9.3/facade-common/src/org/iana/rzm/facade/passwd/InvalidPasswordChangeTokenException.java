package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public class InvalidPasswordChangeTokenException extends PasswordChangeException {
    public InvalidPasswordChangeTokenException(String userName) {
        super(userName);
    }

    public InvalidPasswordChangeTokenException(String userName, String message) {
        super(userName, message);
    }

    public InvalidPasswordChangeTokenException(String userName, String message, Throwable cause) {
        super(userName, message, cause);
    }

    public InvalidPasswordChangeTokenException(String userName, Throwable cause) {
        super(userName, cause);
    }
}
