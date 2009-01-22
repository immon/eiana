package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public class PasswordChangeNotInitiatedException extends PasswordChangeException {
    public PasswordChangeNotInitiatedException(String userName) {
        super(userName);
    }

    public PasswordChangeNotInitiatedException(String userName, String message) {
        super(userName, message);
    }

    public PasswordChangeNotInitiatedException(String userName, String message, Throwable cause) {
        super(userName, message, cause);
    }

    public PasswordChangeNotInitiatedException(String userName, Throwable cause) {
        super(userName, cause);
    }
}
