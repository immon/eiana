package org.iana.rzm.facade.auth;

import org.iana.rzm.facade.user.UserVO;

import java.sql.Timestamp;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticatedUser extends UserVO {

    public static enum Authenticator {
        SECUREID,
        PASSWORD
        /* PGP? */
    }

    private Timestamp lastAccess;
    private Set<Authenticator> receivedAuths;
    private Set<Authenticator> requiredAuths;

    AuthenticatedUser() {
    }

    public boolean isAdditionalAuthenticationRequired() {
        Set<Authenticator> auths = new HashSet<Authenticator>(requiredAuths);
        auths.removeAll(receivedAuths);
        return !auths.isEmpty();
    }

    public boolean isSecureIDRequired() {
        return requiredAuths.contains(Authenticator.SECUREID)
            && !receivedAuths.contains(Authenticator.SECUREID);
    }

    public void checkPermission(Permission permission) throws AccessDeniedException {
        if (isAdditionalAuthenticationRequired()) throw new AccessDeniedException("additional authentication required");
        // todo...
    }
}
