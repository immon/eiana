package org.iana.rzm.facade.system.trans;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionsVO;

/**
 * The service to detect changes between a modified and current domain.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface TransactionDetectorService extends RZMStatefulService {

    TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

}
