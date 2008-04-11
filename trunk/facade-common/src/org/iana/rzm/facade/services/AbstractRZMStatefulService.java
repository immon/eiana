package org.iana.rzm.facade.services;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.*;

import java.util.*;

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

    protected AbstractRZMStatefulService() {
    }

    protected AbstractRZMStatefulService(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager");
        this.userManager = userManager;
    }

    public void setUserManager(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager");
        this.userManager = userManager;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        this.user = null;
    }

    protected RZMUser getRZMUser() throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser ret = userManager.get(user.getObjId());
        if (ret == null) throw new AccessDeniedException("rzm user not found");
        if (!ret.isActive()) throw new AccessDeniedException("user " + ret.getName() + " is not active");
        return ret;
    }

    protected Set<String> getRoleDomainNames() {
        Set<String> ret = new HashSet<String>();
        RZMUser rzmUser = getRZMUser();
        for (Role role : rzmUser.getRoles()) {
            if (role instanceof SystemRole) {
                ret.add(((SystemRole) role).getName());
            }
        }
        return ret;
    }

    public static final Role IANA = new AdminRole(AdminRole.AdminType.IANA);

    public static final Role GOV = new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT);

    public static final List<Role> IANA_GOV = Arrays.asList(IANA, GOV);

    final protected void isIanaOrGOV() throws AccessDeniedException {
        isUserInRole(IANA_GOV);
    }

    final protected void isIana() throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser();
        if (!rzmUser.isInRole(IANA)) throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    final protected void isGov() throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser();
        if (!rzmUser.isInRole(GOV)) throw new AccessDeniedException("authenticated user not in the role GOV_OVERSIGHT");
    }

    final protected void isAdmin() throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser();
        if (!rzmUser.isAdmin()) throw new AccessDeniedException("authenticated user not in the admin role");
    }


    final protected void isUserInIanaOrDomainRole(String domainName) throws AccessDeniedException {
        if (domainName == null) throw new AccessDeniedException("null domain");
        List<Role> roles = new ArrayList<Role>();
        roles.add(IANA);
        roles.add(new SystemRole(SystemRole.SystemType.AC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.TC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.SO, domainName));
        isUserInRole(roles);
    }

    final protected void isUserInIanaGovOrDomainRole(String domainName) throws AccessDeniedException {
        if (domainName == null) throw new AccessDeniedException("null domain");
        List<Role> roles = new ArrayList<Role>();
        roles.add(IANA);
        roles.add(GOV);
        roles.add(new SystemRole(SystemRole.SystemType.AC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.TC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.SO, domainName));
        isUserInRole(roles);
    }

    final protected void isUserInRole(Collection<Role> roles) throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser();
        if (!rzmUser.isInAnyRole(roles)) throw new AccessDeniedException("authenticated user not in the role: " + roles);
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return user;
    }

    public UserVO getUser() {
        return UserConverter.convert(getRZMUser());
    }
    
}
