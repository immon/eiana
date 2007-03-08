package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;

/**
 * org.iana.rzm.facade.auth.SecurIDAuthenticator
 *
 * @author Marcin Zajaczkowski
 */
public class SecurIDAuthenticator implements Authenticator {


    public AuthenticatedUser authenticate(AuthenticationData data, UserManager userManager) throws AuthenticationException {
        throw new IllegalStateException("Not implemented yet.");
    }
}
