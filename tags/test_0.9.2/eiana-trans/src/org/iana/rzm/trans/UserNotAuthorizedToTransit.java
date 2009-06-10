package org.iana.rzm.trans;

/**
 * @author Jakub Laszkiewicz
 */
public class UserNotAuthorizedToTransit extends TransactionException {
    public UserNotAuthorizedToTransit() {
        super();
    }

    public UserNotAuthorizedToTransit(String message) {
        super(message);
    }

    public UserNotAuthorizedToTransit(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotAuthorizedToTransit(Throwable cause) {
        super(cause);
    }
}
