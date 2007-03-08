package org.iana.rzm.facade.auth;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.RoleVO;

import java.sql.Timestamp;
import java.util.Set;
import java.security.Permission;

/**
 * <p>This class represents an authenticated user of the system i.e. an individual who provided valid credentials
 * and got identified by the system.</p>
 *
 * <p>Note that to obtain an <code>AuthenticatedUser</code> instance <code>AuthenticationService</code>
 * may be required to be called several times, like, the system policy may force a user to get identified
 * via both a password and SecurID token.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticatedUser {

    /**
     * A user who got authenticated.
     */
    private UserVO user;

    /**
     * <p>A package-accessible constructor to protect creation of this class outside of the package,
     * thus one is forced to perform authentication in order to get access to the system services.
     * (Tough it's still possible for a developer to inject a class into this package and then obtain
     * a valid instance of an <code>AuthenticatedUser</code>, however this would mean a deliberate
     * action taken by a developer.)</p>
     *
     * @param user the user to be represented by this authenticated user
     * @throws IllegalArgumentException when user is null
     */
    AuthenticatedUser(UserVO user) {
        this.user = user;
    }

    /**
     * Checks whether this user has been granted a given permission.
     *
     * @param permission the permission to be checked
     * @throws AccessDeniedException when this user has not been granted this permission
     * @throws AuthenticationRequiredException when this user requires to re-authenticate in the system. This exception
     * may be caused by the permission itself (a vulnerable operation that requires re-login) or by a timed-out user
     * session.
     * @throws UserInvalidatedException
     */
    public void checkPermission(Permission permission) throws AccessDeniedException, AuthenticationRequiredException {
        // todo
    }

    /**
     * Invalidates this user object. After the invalidation happens the object can not be used as a valid
     * authenticated object. Every call to <code>checkPermission</code> will raise a
     * <code>UserInvalidatedException</code>.
     */
    public void invalidate() {
        // todo
    }

    public String getUserName() {
        return user.getUserName();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getOrganization() {
        return user.getOrganization();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public Set<RoleVO> getRoles() {
        return user.getRoles();
    }

    public Long getObjId() {
        return user.getObjId();
    }

    public Timestamp getCreated() {
        return user.getCreated();
    }

    public Timestamp getModified() {
        return user.getModified();
    }

    public String getCreatedBy() {
        return user.getCreatedBy();
    }

    public String getModifiedBy() {
        return user.getModifiedBy();
    }
}
