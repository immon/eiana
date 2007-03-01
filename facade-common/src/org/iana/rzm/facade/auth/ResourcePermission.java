package org.iana.rzm.facade.auth;

import java.security.BasicPermission;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class ResourcePermission extends BasicPermission {

    protected ResourcePermission(String name) {
        super(name);
    }

    abstract protected void isGranted(AuthenticatedUser user) throws AccessDeniedException;
}
