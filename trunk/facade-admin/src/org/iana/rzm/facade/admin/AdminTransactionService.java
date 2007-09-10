package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.notification.NotificationAddresseeVO;
import org.iana.rzm.facade.system.notification.NotificationVO;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminTransactionService extends RZMStatefulService, AdminFinderService<TransactionVO> {

    TransactionVO getTransaction(long id) throws NoTransactionException, AccessDeniedException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException;

    TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException, InvalidCountryCodeException, AccessDeniedException;

    TransactionVO createDomainModificationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainModificationException, InvalidCountryCodeException, AccessDeniedException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoTransactionException, InfrastructureException, InvalidCountryCodeException;

    /**
     * Creates the transaction splitting the transactions based on the splitNameServerChange flag.
     *
     * Note that the transaction may be splitted regardless of split name server changes flag when there is a need
     * to split it in case of parties impacted by the name server change.
     *
     * @param domain the modified domain.
     * @param splitNameServerChange the flag indicating that the transaction is to be splitted if the name servers are modified.
     * @param submitterEmail
     * @return the created transactions.
     * @throws org.iana.rzm.facade.auth.AccessDeniedException when access is denied to the user or no user is set.
     * @throws org.iana.rzm.facade.common.NoObjectFoundException when no domain found with the specified name.
     * @throws org.iana.rzm.common.exceptions.InfrastructureException when an internal error occured during processing of this method.
     */
    List<TransactionVO> createDomainModificationTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException;

    void acceptTransaction(long id) throws NoTransactionException, FacadeTransactionException, AccessDeniedException;

    void rejectTransaction(long id) throws NoTransactionException, FacadeTransactionException, AccessDeniedException;

    void transitTransaction(long id, String transitionName) throws NoTransactionException, FacadeTransactionException, AccessDeniedException;

    void updateTransaction(long id, Long ticketId, String targetStateName, boolean redelegation) throws NoTransactionException, StateUnreachableException, FacadeTransactionException, AccessDeniedException;

    void updateTransaction(long id, Long ticketId, TransactionStateVO.Name targetStateName, boolean redelegation) throws NoTransactionException, StateUnreachableException, FacadeTransactionException, AccessDeniedException;

    List<TransactionVO> findAll() throws AccessDeniedException;

    void setTransactionTicketId(long transactionID, long ticketId) throws NoTransactionException, AccessDeniedException;

    List<TransactionVO> find(TransactionCriteriaVO criteria) throws AccessDeniedException;

    public List<TransactionVO> findTransactions(Criterion criteria);

    public void deleteTransaction(TransactionVO transaction) throws NoTransactionException, AccessDeniedException;

    public void deleteTransaction(long transactionId) throws NoTransactionException, AccessDeniedException;

    public int count(Criterion criteria);

    public List<TransactionVO> find(Criterion criteria, int offset, int limit);

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException;

    public void resendNotification(Set<NotificationAddresseeVO> addressees, NotificationVO notification) throws NotificationException;
}
