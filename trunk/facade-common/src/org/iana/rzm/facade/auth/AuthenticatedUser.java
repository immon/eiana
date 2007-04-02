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
     * A flag representing whether the user has been invalidated.
     */
    private boolean invalidated;

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
        this.invalidated = false;
    }

    /**
     * Determines whether the user is in a given role.
     *
     * @param role the role that must be checked the user is in.
     * @throws AccessDeniedException when this user is not is the role
     * @throws AuthenticationRequiredException when this user requires to re-authenticate in the system.
     * This exception may be caused, for example, by a timed-out session.
     * @throws UserInvalidatedException when the user has been invalidated.
     */
    public void isInRole(RoleVO role) throws AccessDeniedException, AuthenticationRequiredException {
        if (invalidated) throw new UserInvalidatedException(user.getUserName());
        if (!user.hasRole(role)) throw new AccessDeniedException("user " + user.getUserName() + " not in a role " + role.getType());
    }

    /**
     * Determines whether the user is in a one of a given roles.
     *
     * @param roles the set of the roles that must be checked the user is in.
     * @throws AccessDeniedException when this user is not is the role
     * @throws AuthenticationRequiredException when this user requires to re-authenticate in the system.
     * This exception may be caused, for example, by a timed-out session.
     * @throws UserInvalidatedException when the user has been invalidated.
     */
    public void isInAnyRole(Set<RoleVO> roles) throws AccessDeniedException, AuthenticationRequiredException {
         if (invalidated) throw new UserInvalidatedException(user.getUserName());
         if (!user.hasAnyRole(roles)) throw new AccessDeniedException("user " + user.getUserName() + " not in any role");
     }

    public boolean hasRole(RoleVO role) {
        return user.hasRole(role);    
    }

    /**
     * Invalidates this user object. After the invalidation happens the object can not be used as a valid
     * authenticated object. Every call to <code>checkPermission</code> will raise a
     * <code>UserInvalidatedException</code>.
     */
    public void invalidate() {
        invalidated = false;
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

    public Set<String> getRoleDomainNames() {
        return user.getRoleDomainNames();
    }
}
