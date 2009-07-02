package org.iana.rzm.facade.system.trans;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionsVO;
import org.iana.rzm.facade.user.UserVOManager;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class TransactionDetectorImpl extends AbstractRZMStatefulService implements TransactionDetectorService {

    private StatelessTransactionDetectorService statelessTransactionDetectorService;

    public TransactionDetectorImpl(UserVOManager userManager, StatelessTransactionDetectorService statelessTransactionDetectorService) {
        super(userManager);
        CheckTool.checkNull(statelessTransactionDetectorService, "stateless transaction detector service");
        this.statelessTransactionDetectorService = statelessTransactionDetectorService;
    }


    public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return detectTransactionActions(domain, PerformTechnicalCheck.ON);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionDetectorService.detectTransactionActions(domain, getAuthenticatedUser(), performTechnicalCheck);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return detectTransactionActions(domain, config, PerformTechnicalCheck.ON);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionDetectorService.detectTransactionActions(domain, config, getAuthenticatedUser(), performTechnicalCheck);
    }
}
