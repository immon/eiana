package org.iana.rzm.facade.common;

import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;
import java.util.Comparator;
import java.util.HashSet;

/**
 * A helper implementation of <code>RZMStatefulService</code>. It contains
 * a placeholder for an <code>AuthenticatedUser</code> instance and provides
 * some helper checks whether a user is in given roles.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class AbstractRZMStatefulService implements RZMStatefulService {

    protected UserManager userManager;
    protected AuthenticatedUser user;

    protected AbstractRZMStatefulService(UserManager userManager) {
        CheckTool.checkNull(user, "user manager");
        this.userManager = userManager;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        this.user = null;
    }

    final protected RZMUser getUser() {
        return userManager.get(user.getObjId());
    }

    final protected Set<String> getRoleDomainNames() {
        Set<String> ret = new HashSet<String>();
        RZMUser rzmUser = getUser();
        for (Role role : rzmUser.getRoles()) {
            if (role instanceof SystemRole) {
                ret.add(((SystemRole) role).getName());
            }
        }
        return ret;
    }

    final protected void isUserInRole(Role role) throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser rzmUser = getUser();
        if (!rzmUser.isInRole(role, roleComparator)) throw new AccessDeniedException("authenticated user not in role: " + role);            
    }

    final protected void isUserInRole(Set<Role> roles) throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser rzmUser = getUser();
        if (!rzmUser.isInAnyRole(roles, roleComparator)) throw new AccessDeniedException("authenticated user not in role: " + roles);
    }

    static RoleComparator roleComparator = new RoleComparator();

    public static class RoleComparator implements Comparator<Role> {
        public int compare(Role o1, Role o2) {
            if (o1.getClass() != o2.getClass()) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
            return o1.getType().toString().compareTo(o2.getType().toString());
        }
    }
}
