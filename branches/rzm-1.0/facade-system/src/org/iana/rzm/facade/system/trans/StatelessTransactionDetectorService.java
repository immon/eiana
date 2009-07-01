package org.iana.rzm.facade.system.trans;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionsVO;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessTransactionDetectorService {

    TransactionActionsVO detectTransactionActions(IDomainVO domain, AuthenticatedUser authUser, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config, AuthenticatedUser authUser, PerformTechnicalCheck performTechnicalCheck) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException;

}
