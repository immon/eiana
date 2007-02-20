package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationService {

    AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationException; 
}
