package org.iana.rzm.facade.services;

import org.iana.rzm.user.*;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.common.validators.CheckTool;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
abstract public class AbstractRZMStatelessService {

    UserManager userManager;

    public AbstractRZMStatelessService(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setUserManager(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager");
        this.userManager = userManager;
    }

    protected RZMUser getRZMUser(AuthenticatedUser user) throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser ret = userManager.get(user.getObjId());
        if (ret == null) throw new AccessDeniedException("rzm user not found");
        if (!ret.isActive()) throw new AccessDeniedException("user " + ret.getName() + " is not active");
        return ret;
    }

    protected Set<String> getRoleDomainNames(AuthenticatedUser user) {
        Set<String> ret = new HashSet<String>();
        RZMUser rzmUser = getRZMUser(user);
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

    final protected void isIanaOrGOV(AuthenticatedUser user) throws AccessDeniedException {
        isUserInRole(IANA_GOV, user);
    }

    final protected void isIana(AuthenticatedUser user) throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser(user);
        if (!rzmUser.isInRole(IANA)) throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    final protected void isGov(AuthenticatedUser user) throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser(user);
        if (!rzmUser.isInRole(GOV)) throw new AccessDeniedException("authenticated user not in the role GOV_OVERSIGHT");
    }

    final protected void isAdmin(AuthenticatedUser user) throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser(user);
        if (!rzmUser.isAdmin()) throw new AccessDeniedException("authenticated user not in the admin role");
    }


    final protected void isUserInIanaOrDomainRole(String domainName, AuthenticatedUser user) throws AccessDeniedException {
        if (domainName == null) throw new AccessDeniedException("null domain");
        List<Role> roles = new ArrayList<Role>();
        roles.add(IANA);
        roles.add(new SystemRole(SystemRole.SystemType.AC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.TC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.SO, domainName));
        isUserInRole(roles, user);
    }

    final protected void isUserInIanaGovOrDomainRole(String domainName, AuthenticatedUser user) throws AccessDeniedException {
        if (domainName == null) throw new AccessDeniedException("null domain");
        List<Role> roles = new ArrayList<Role>();
        roles.add(IANA);
        roles.add(GOV);
        roles.add(new SystemRole(SystemRole.SystemType.AC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.TC, domainName));
        roles.add(new SystemRole(SystemRole.SystemType.SO, domainName));
        isUserInRole(roles, user);
    }

    final protected void isUserInRole(Collection<Role> roles, AuthenticatedUser user) throws AccessDeniedException {
        RZMUser rzmUser = getRZMUser(user);
        if (!rzmUser.isInAnyRole(roles)) throw new AccessDeniedException("authenticated user not in the role: " + roles);
    }

    public UserVO getUser(AuthenticatedUser user) {
        return UserConverter.convert(getRZMUser(user));
    }
}
