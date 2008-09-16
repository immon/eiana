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

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class TransactionDetectorImpl extends AbstractRZMStatefulService implements TransactionDetectorService {

    private StatelessTransactionDetectorService statelessTransactionDetectorService;

    public TransactionDetectorImpl(StatelessTransactionDetectorService statelessTransactionDetectorService) {
        CheckTool.checkNull(statelessTransactionDetectorService, "stateless transaction detector service");
        this.statelessTransactionDetectorService = statelessTransactionDetectorService;
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionDetectorService.detectTransactionActions(domain, getAuthenticatedUser());
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionDetectorService.detectTransactionActions(domain, config, getAuthenticatedUser());
    }
}
