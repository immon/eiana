package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.user.*;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * @author: Piotr Tkaczyk
 *
 */

public class GuardedAdminDomainServiceBean extends AbstractRZMStatefulService implements AdminDomainService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    DomainManager domainManager;


    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminDomainServiceBean(UserManager userManager, DomainManager domainManager) {
        super(userManager);
        CheckTool.checkNull(domainManager, "domain manager");
        this.domainManager = domainManager;
    }

    public DomainVO getDomain(String domainName) {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "domain name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + domainName);
        return ToVOConverter.toDomainVO(retrivedDomain);
    }

    public DomainVO getDomain(long id) {
        isUserInRole();
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        return ToVOConverter.toDomainVO(retrivedDomain);
    }

    public void createDomain(DomainVO domain) {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = FromVOConverter.toDomain(domain);
        domainManager.create(newDomain);
    }

    public void updateDomain(DomainVO domain) {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = FromVOConverter.toDomain(domain);
        domainManager.update(newDomain);
    }

    public void deleteDomain(String domainName) {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "doamain Name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain");
        domainManager.delete(retrivedDomain);
    }

    public void deleteDomain(long id) {
        isUserInRole();
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        domainManager.delete(retrivedDomain);
    }

    public List<DomainVO> findDomains() {
        isUserInRole();
        List<DomainVO> domainVOList = new ArrayList<DomainVO>();
        for (Domain domian: domainManager.findAll())
            domainVOList.add(ToVOConverter.toDomainVO(domian));
        return domainVOList;
    }

    public List<DomainVO> findDomains(Criterion criteria) {
        isUserInRole();
        //todo
        return null;
    }

    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
    }

}
