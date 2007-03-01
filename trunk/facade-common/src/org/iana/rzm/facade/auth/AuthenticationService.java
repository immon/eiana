package org.iana.rzm.facade.auth;

import javax.mail.AuthenticationFailedException;


/**
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationService {

    /**
     *
     * @param data the data allowing to authenticate a user; specific to the authentication type
     * @return the authenticated user in case of successful authentication
     * @throws AuthenticationFailedException thrown when a provided data do not form a valid credential
     * @throws AuthenticationRequiredException thrown when an additional authentication is required
     */
    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException;

    /**
     *
     * @param token
     * @param data the data allowing to authenticate a user; specific to the authentication type
     * @return the authenticated user in case of successful authentication
     * @throws AuthenticationFailedException thrown when a provided data do not form a valid credential
     * @throws AuthenticationRequiredException thrown when an additional authentication is required
     */
    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException;
}
