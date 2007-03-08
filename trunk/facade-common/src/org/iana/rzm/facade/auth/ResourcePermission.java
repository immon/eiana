package org.iana.rzm.facade.auth;

import java.security.BasicPermission;

/**
 * <p>This class represents a permission resolved based on a resource and user context. For example,
 * a user can be granted to modify only his/her own profile.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public abstract class ResourcePermission extends BasicPermission {

    protected ResourcePermission(String name) {
        super(name);
    }

    abstract protected void isGranted(AuthenticatedUser user) throws AccessDeniedException;
}
