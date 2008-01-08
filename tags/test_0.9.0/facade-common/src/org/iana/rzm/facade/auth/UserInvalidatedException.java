package org.iana.rzm.facade.auth;

/**
 * <p>This exception is thrown when an authentication is required since a user has been invalidated.</p> 
 *
 * @author Patrycja Wegrzynowicz
 */
public class UserInvalidatedException extends AuthenticationRequiredException {

    public UserInvalidatedException(String userName) {
        super(new AuthenticationToken(userName), Authentication.PASSWORD);
    }

    public UserInvalidatedException(String userName, Authentication required) {
        super(new AuthenticationToken(userName), required);
    }

    public UserInvalidatedException(AuthenticationToken token, Authentication required) {
        super(token, required);
    }
}
