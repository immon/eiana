package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ResourcePermission {

    void isGranted(AuthenticatedUser user) throws AccessDeniedException;
}
