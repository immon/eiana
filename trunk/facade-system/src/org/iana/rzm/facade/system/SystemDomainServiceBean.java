package org.iana.rzm.facade.system;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.DomainException;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class SystemDomainServiceBean implements SystemDomainService {

    private DomainManager domainManager;
    private UserManager userManager;

    public SystemDomainServiceBean(DomainManager domainManager, UserManager userManager) {
        this.domainManager = domainManager;
        this.userManager = userManager;
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        try {
            Domain domain = domainManager.get(id);
            if (domain == null) throw new NoObjectFoundException(id);
            DomainVO domainVO = new DomainVO();
            ToVOConverter.convertToDomainVO(domain, domainVO);
            return domainVO;
        } catch (DomainException e) {
            throw new InfrastructureException();
        }
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        try {
            Domain domain = domainManager.get(name);
            if (domain == null) throw new NoObjectFoundException(name);
            DomainVO domainVO = new DomainVO();
            ToVOConverter.convertToDomainVO(domain, domainVO);
            return domainVO;
        } catch (DomainException e) {
            throw new InfrastructureException();
        }
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        List<SimpleDomainVO> list = new ArrayList<SimpleDomainVO>();
        try {
            RZMUser user = userManager.get(userName);
            if(user instanceof SystemUser) {
                List<Role> roles = ((SystemUser) user).getRoles();
                Set<String> roleNames = new HashSet<String>();
                for(Iterator iterator = roles.iterator(); iterator.hasNext();)
                    roleNames.add(((Role) iterator.next()).getName());

                for(Iterator iterator = roleNames.iterator(); iterator.hasNext();) {
                    String roleName = (String) iterator.next();
                    Domain domain = domainManager.get(roleName);
                    if (domain != null) {
                        SimpleDomainVO simpleDomainVO = new SimpleDomainVO();
                        ToVOConverter.convertToSimpleDomainVO(domain,  simpleDomainVO);
                        list.add(simpleDomainVO);
                    }
                }
            } else
                throw new AccessDeniedException("not system user");
        } catch (DomainException e) {
            //todo
        }
        return list;
    }

    public void setUser(AuthenticatedUser user) {
        //empty
    }

    public void close() {
        //todo
    }
}
