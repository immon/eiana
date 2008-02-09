package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public class NewPasswordConfirmationMismatchException extends PasswordChangeException {
    public NewPasswordConfirmationMismatchException(String userName) {
        super(userName);
    }

    public NewPasswordConfirmationMismatchException(String userName, String message) {
        super(userName, message);
    }

    public NewPasswordConfirmationMismatchException(String userName, String message, Throwable cause) {
        super(userName, message, cause);
    }

    public NewPasswordConfirmationMismatchException(String userName, Throwable cause) {
        super(userName, cause);
    }
}
