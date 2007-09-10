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
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public interface AdminTransactionService extends RZMStatefulService, AdminFinderService<TransactionVO> {

    TransactionVO getTransaction(long id) throws NoTransactionException, AccessDeniedException;

    void setIgnoreTicketingSystemErrors(boolean ignore);

    boolean getIgnoreTicketingSystemErrors();

    TransactionVO createDomainCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException, CreateTicketException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException, CreateTicketException;

    TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException, InvalidCountryCodeException, AccessDeniedException, CreateTicketException;

    TransactionVO createDomainModificationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainModificationException, InvalidCountryCodeException, AccessDeniedException, CreateTicketException;

    TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoTransactionException, InfrastructureException, InvalidCountryCodeException;

    List<TransactionVO> createDomainModificationTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, CreateTicketException;

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
