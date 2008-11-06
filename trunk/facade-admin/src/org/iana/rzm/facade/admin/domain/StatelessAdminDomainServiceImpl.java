package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.Criterion;
import org.iana.criteria.In;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.exporter.DomainExporter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */

public class StatelessAdminDomainServiceImpl implements StatelessAdminDomainService {

    DomainManager domainManager;

    DomainExporter domainExporter;

    public StatelessAdminDomainServiceImpl(DomainManager domainManager, DomainExporter domainExporter) {
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(domainExporter, "domain exporter");
        this.domainManager = domainManager;
        this.domainExporter = domainExporter;
    }

    public IDomainVO getDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkEmpty(domainName, "domain name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + domainName);
        return DomainToVOConverter.toDomainVO(retrivedDomain);
    }

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        return DomainToVOConverter.toDomainVO(retrivedDomain);
    }

    public void createDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException {
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = DomainFromVOConverter.toDomain(domain);
        newDomain.setCreated(new Timestamp(System.currentTimeMillis()));
        newDomain.setCreatedBy(authUser.getUserName());
        domainManager.create(newDomain);
    }

    public void updateDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException {
        CheckTool.checkNull(domain, "domainVO");
        Domain newDomain = DomainFromVOConverter.toDomain(domain);
        domainManager.update(newDomain, authUser.getUserName());
    }

    public void updateSpecialReviewOnly(List<IDomainVO> domains, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkCollectionNull(domains, "List<IDomainVO>");
        Map<String, Boolean> domainsMap = new HashMap<String, Boolean>();
        for (IDomainVO d : domains) {
            CheckTool.checkEmpty(d.getName(), "domain name");
            CheckTool.checkNull(d.isSpecialReview(), "special review flag");
            domainsMap.put(d.getName(), d.isSpecialReview());
        }
        Criterion criteria = new In("name.name", domainsMap.keySet());
        for (Domain d : domainManager.find(criteria)) {
            d.setSpecialReview(domainsMap.get(d.getName()));
        }
    }

    public void deleteDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkEmpty(domainName, "doamain Name");
        Domain retrivedDomain = domainManager.get(domainName);
        CheckTool.checkNull(retrivedDomain, "no such domain");
        domainManager.delete(retrivedDomain);
    }

    public void deleteDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        Domain retrivedDomain = domainManager.get(id);
        CheckTool.checkNull(retrivedDomain, "no such domain: " + id);
        domainManager.delete(retrivedDomain);
    }

    public List<IDomainVO> findDomains(AuthenticatedUser authUser) throws AccessDeniedException {
        List<IDomainVO> domainVOList = new ArrayList<IDomainVO>();
        for (Domain domian: domainManager.findAll())
            domainVOList.add(DomainToVOConverter.toSimpleDomainVO(domian));
        return domainVOList;
    }

    public List<IDomainVO> findDomains(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }


    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return domainManager.count(criteria);
    }

    public List<IDomainVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria, offset, limit))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }

    public IDomainVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getDomain(id, authUser);
    }

    public List<IDomainVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }


    public void exportDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        domainExporter.exportToXML(domainManager.findAll());
    }

    public String saveDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return domainExporter.saveToXML(domainManager.findAll());
    }

    public List<IDomainVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        List<IDomainVO> domainVOs = new ArrayList<IDomainVO>();
        for (Domain domain : domainManager.find(criteria, offset, limit))
            domainVOs.add(DomainToVOConverter.toSimpleDomainVO(domain));
        return domainVOs;
    }
}
