package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.DomainVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminDomainService extends RZMStatefulService {

    public DomainVO getDomain(String DomainName);

    public DomainVO getDomain(long id);

    public void createDomain(DomainVO Domain);

    public void updateDomain(DomainVO Domain);

    public void deleteDomain(String DomainName);

    public void deleteDomain(long id);

    public List<DomainVO> findDomains();

    public List<DomainVO> findDomains(DomainCriteria criteria);
}
