package org.iana.rzm.facade.common;

import org.iana.rzm.facade.auth.AuthenticatedUser;

/**
 * <p>This interface represents a stateful service intented to be used in a context of an authenticated user.</p>
 * <p>Services that extend this interface should mark other service methods as throwing <code>AccessDeniedException</code>,
 * <code>AuthenticationRequiredException</code>, and <code>UserInvalidatedException</code>.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface RZMStatefulService {

    /**
     * <p>Associates a given user with this service. All other service methods will be executed
     * in a context of this user i.e. this user will be checked against having respective permissions to perform
     * a specific service method.</p>
     *
     * @param user
     */
    public void setUser(AuthenticatedUser user);

    /**
     * <p>Closes this service.</p>
     * <p>Note that the service may be closed implicitely when the user is invalidated.</p>
     */
    public void close();
}
