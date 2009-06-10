package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public class UserNotFoundException extends PasswordChangeException {
    public UserNotFoundException(String userName) {
        super(userName);
    }

    public UserNotFoundException(String userName, String message) {
        super(userName, message);
    }

    public UserNotFoundException(String userName, String message, Throwable cause) {
        super(userName, message, cause);
    }

    public UserNotFoundException(String userName, Throwable cause) {
        super(userName, cause);
    }
}
