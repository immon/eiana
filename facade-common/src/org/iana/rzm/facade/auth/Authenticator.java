package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;

/**
 * org.iana.rzm.facade.auth.Authenticator
 *
 * @author Marcin Zajaczkowski
 */
public interface Authenticator {

    public AuthenticatedUser authenticate(AuthenticationData data, UserManager userManager) throws AuthenticationException;
}
