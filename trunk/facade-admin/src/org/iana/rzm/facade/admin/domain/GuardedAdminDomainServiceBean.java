package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.exporter.DomainExporter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 *
 */

public class GuardedAdminDomainServiceBean extends AbstractFinderService<IDomainVO> implements AdminDomainService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    DomainManager domainManager;

    DomainExporter domainExporter;

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminDomainServiceBean(UserManager userManager, DomainManager domainManager, DomainExporter domainExporter) {
        super(userManager);
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(domainExporter, "domain exporter");
        this.domainManager = domainManager;
        this.domainExporter = domainExporter;
    }

    public IDomainVO getDomain(String domainName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "domain name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + domainName);
        return DomainToVOConverter.toDomainVO(retrivedDomain);
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException {
        isUserInRole();
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        return DomainToVOConverter.toDomainVO(retrivedDomain);
    }

    public void createDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = DomainFromVOConverter.toDomain(domain);
        domainManager.create(newDomain);
    }

    public void updateDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = DomainFromVOConverter.toDomain(domain);
        newDomain.setModified(new Timestamp(System.currentTimeMillis()));
        newDomain.setModifiedBy(user.getUserName());
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
            domainVOList.add(DomainToVOConverter.toSimpleDomainVO(domian));
        return domainVOList;
    }

    public List<IDomainVO> findDomains(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
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
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }

    public IDomainVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getDomain(id);
    }

    public List<IDomainVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }


    public void exportDomainsToXML() throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        domainExporter.exportToXML(domainManager.findAll());
    }
}
