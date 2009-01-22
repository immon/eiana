package org.iana.rzm.facade.passwd;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class PasswordChangeException extends Exception {
    private String userName;

    public PasswordChangeException(String userName) {
        super();
        this.userName = userName;
    }

    public PasswordChangeException(String userName, String message) {
        super(message);
        this.userName = userName;
    }

    public PasswordChangeException(String userName, String message, Throwable cause) {
        super(message, cause);
        this.userName = userName;
    }

    public PasswordChangeException(String userName, Throwable cause) {
        super(cause);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
