package org.iana.rzm.facade.auth;

/**
 * <p>
 * This exception is thrown when a user is not permitted to access a specified resource like service method or object attribute.
 * For example, specialInstruction attribute of domain object is accessibly only by IANA staff.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
