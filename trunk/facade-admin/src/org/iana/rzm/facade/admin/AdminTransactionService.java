package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.notification.NotificationAddresseeVO;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.*;

import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
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

    /**
     * Gets contacts and USDoC confirmation notifiactions for the transaction
     * identified by given <code>transactionId</code>.
     * @param transactionId identifier of the transaction for which contacts and USDoC confirmation
     * notifiactions have to be returned.
     * @return contacts and USDoC confirmation notifiactions for the transaction
     * identified by given <code>transactionId</code>.
     * @throws InfrastructureException when getting notifications failed.
     */
    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException;

    /**
     * Gets notifications specified by given <code>criteria</code>. Field names which can be
     * used with <code>criteria</code> are specified in <code>NotificationCriteriaFields</code>
     * interface.
     * @see Criterion
     * @see org.iana.notifications.NotificationCriteriaFields
     * @param criteria specify notifications to be returned.
     * @return notifications specified by given <code>criteria</code>.
     * @throws InfrastructureException when getting notifications failed.
     */
    public List<NotificationVO> getNotifications(Criterion criteria) throws InfrastructureException;

    /**
     * Redends any persisted notification identified by <code>notificationId</code>
     * to given <code>addressees<code>. Notification body content is preceded with given
     * <code>comment</comment>.
     * @param addressees specifies to whom the notification has to be sent.
     * @param notificationId identifier of the notification to be resent.
     * @param comment to be added at the beginning of the notification body content.
     * @throws InfrastructureException when resending notification failed.
     */
    public void resendNotification(Set<NotificationAddresseeVO> addressees, Long notificationId, String comment) throws InfrastructureException;

    /**
     * Redends confirmation notifiactions of given <code>type</code> for the transaction identified
     * by given <code>transactionId</code> to addressees, who did not confirmed yet.
     * Notification body content is preceded with given <code>comment</comment>.
     * @param transactionId identifier of the transaction for which confirmation notifiactions
     * have to be resent.
     * @param type of the notifications to be resent.
     * @see NotificationVO.Type
     * @param comment to be added at the beginning of the notification body content.
     * @throws InfrastructureException when resending notification failed.
     */
    public void resendNotification(Long transactionId, NotificationVO.Type type, String comment) throws InfrastructureException, FacadeTransactionException;
}
