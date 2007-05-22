package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminDomainService extends RZMStatefulService {

    public IDomainVO getDomain(String domainName);

    public IDomainVO getDomain(long id);

    public void createDomain(IDomainVO domain);

    public void updateDomain(IDomainVO domain);

    public void deleteDomain(String domainName);

    public void deleteDomain(long id);

    public List<IDomainVO> findDomains();

    public List<IDomainVO> findDomains(Criterion criteira);

    public int count(Criterion criteria);

    public List<IDomainVO> find(Criterion criteria, int offset, int limit);
}
