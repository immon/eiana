package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.FinderService;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;

import java.util.List;

/**
 * This interface provides a set of facade method to manipulate domain modification transactions.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface TransactionService extends RZMStatefulService, FinderService<TransactionVO> {

    List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void withdrawTransaction(long id) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException;

    // temporary method - not to break the tests

    void moveTransactionToNextState(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException;

    void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;
}
