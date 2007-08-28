package org.iana.rzm.facade.admin;

import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.system.converter.*;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 *
 */

public class GuardedAdminDomainServiceBean extends AdminFinderServiceBean<IDomainVO> implements AdminDomainService {

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

    public IDomainVO getDomain(String domainName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "domain name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + domainName);
        return ToVOConverter.toDomainVO(retrivedDomain);
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException {
        isUserInRole();
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        return ToVOConverter.toDomainVO(retrivedDomain);
    }

    public void createDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = FromVOConverter.toDomain(domain);
        domainManager.create(newDomain);
    }

    public void updateDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = FromVOConverter.toDomain(domain);
        domainManager.update(newDomain);
    }

    public void deleteDomain(String domainName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "doamain Name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain");
        domainManager.delete(retrivedDomain);
    }

    public void deleteDomain(long id) throws AccessDeniedException {
        isUserInRole();
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        domainManager.delete(retrivedDomain);
    }

    public List<IDomainVO> findDomains() throws AccessDeniedException {
        isUserInRole();
        List<IDomainVO> domainVOList = new ArrayList<IDomainVO>();
        for (Domain domian: domainManager.findAll())
            domainVOList.add(ToVOConverter.toSimpleDomainVO(domian));
        return domainVOList;
    }

    public List<IDomainVO> findDomains(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria))
            domainVOs.add(ToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }

    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        return domainManager.count(criteria);
    }

    public List<IDomainVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        isUserInRole();
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria, offset, limit))
            domainVOs.add(ToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }
}
