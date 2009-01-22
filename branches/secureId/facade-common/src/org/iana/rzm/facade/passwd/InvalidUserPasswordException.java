package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public class InvalidUserPasswordException extends PasswordChangeException {
    public InvalidUserPasswordException(String userName) {
        super(userName);
    }

    public InvalidUserPasswordException(String userName, String message) {
        super(userName, message);
    }

    public InvalidUserPasswordException(String userName, String message, Throwable cause) {
        super(userName, message, cause);
    }

    public InvalidUserPasswordException(String userName, Throwable cause) {
        super(userName, cause);
    }
}
