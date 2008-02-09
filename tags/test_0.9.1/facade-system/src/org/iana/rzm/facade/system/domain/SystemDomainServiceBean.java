package org.iana.rzm.facade.system.domain;

import org.iana.criteria.In;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class SystemDomainServiceBean extends AbstractRZMStatefulService implements SystemDomainService {

    private DomainManager domainManager;

    public SystemDomainServiceBean(DomainManager domainManager, UserManager userManager) {
        super(userManager);
        this.domainManager = domainManager;
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        Domain domain = domainManager.get(id);
        if (domain == null) throw new NoObjectFoundException(id, "domain");
        try {
            DomainVO domainVO = DomainToVOConverter.toDomainVO(domain);
            RZMUser user = getRZMUser();
            domainVO.setRoles(getRoleTypeByDomainName(user, domainVO.getName()));
            return domainVO;
        } catch (InvalidCountryCodeException e) {
            throw new InfrastructureException(e);
        } catch (InvalidEmailException e) {
            throw new InfrastructureException(e);
        }
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        Domain domain = domainManager.get(name);
        if (domain == null) throw new NoObjectFoundException(name, "domain");
        try {
            DomainVO domainVO = DomainToVOConverter.toDomainVO(domain);
            RZMUser user = getRZMUser();
            domainVO.setRoles(getRoleTypeByDomainName(user, domainVO.getName()));
            return domainVO;
        } catch (InvalidCountryCodeException e) {
            throw new InfrastructureException(e);
        } catch (InvalidEmailException e) {
            throw new InfrastructureException(e);
        }
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        if (user == null) throw new AccessDeniedException("null authenticated user");
        return findUserDomains(user.getUserName());
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        RZMUser user = getRZMUser();

        Map<String, Set<RoleVO.Type>> domainNames = new HashMap<String, Set<RoleVO.Type>>();
        for (Role role : user.getRoles())
            if (!role.isAdmin()) {
                SystemRole sr = (SystemRole) role;
                if (sr.isAccessToDomain()) {
                    String domainName = sr.getName();
                    Set<RoleVO.Type> roles = domainNames.get(domainName);
                    if (roles == null) {
                        roles = new HashSet<RoleVO.Type>();
                    }
                    roles.add(DomainToVOConverter.toRoleTypeVO(sr.getType()));
                    domainNames.put(domainName, roles);
                }
            }

        List<SimpleDomainVO> ret = new ArrayList<SimpleDomainVO>();
        if (!domainNames.isEmpty()) {
            List<Domain> domains = domainManager.find(new In("name.name", new HashSet<Object>(domainNames.keySet())));
            for (Domain domain : domains) {
                if (domain != null) {
                    SimpleDomainVO simpleDomainVO = DomainToVOConverter.toSimpleDomainVO(domain);
                    simpleDomainVO.setRoles(domainNames.get(domain.getName()));
                    ret.add(simpleDomainVO);
                }
            }
        }
        return ret;
    }

    public void setAccessToDomain(long userId, long domainId, boolean access) {
        Domain domain = domainManager.get(domainId);
        RZMUser user = userManager.get(userId);
        for (Role role : user.getRoles()) {
            if (role instanceof SystemRole) {
                SystemRole systemRole = (SystemRole) role;
                if (domain.getName().equals(systemRole.getName()))
                    systemRole.setAccessToDomain(access);
            }
        }
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) {
        List<UserVO> result = new ArrayList<UserVO>();
        List<RZMUser> users = userManager.findUsersInSystemRole(domainName, null, false, false, havingAccessOnly);
        for (RZMUser user : users)
            result.add(UserConverter.convert(user));
        return result;
    }

    private Set<RoleVO.Type> getRoleTypeByDomainName(RZMUser user, String domainName) {
        if ((user.getRoles() == null) || (domainName == null)) return null;
        Set<RoleVO.Type> roleTypeVOSet = new HashSet<RoleVO.Type>();
        for (Role role : user.getRoles())
            if (!role.isAdmin()) {
                SystemRole sr = (SystemRole) role;
                if (sr.isAccessToDomain() && sr.getName().equals(domainName))
                    roleTypeVOSet.add(DomainToVOConverter.toRoleTypeVO(sr.getType()));
            }
        return roleTypeVOSet;
    }
}
