package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.*;
import org.iana.notifications.*;
import org.iana.notifications.exception.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.trans.notifications.*;
import org.iana.rzm.facade.services.*;
import org.iana.rzm.facade.system.notification.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.confirmation.*;
import org.iana.rzm.trans.confirmation.contact.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 *
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class AdminNotificationServiceImpl extends AbstractRZMStatefulService implements AdminNotificationService {

    NotificationManager notificationManager;
    NotificationSender notificationSender;
    TransactionManager transactionManager;


    public AdminNotificationServiceImpl(UserManager userManager, NotificationManager notificationManager, NotificationSender notificationSender, TransactionManager transactionManager) {
        super(userManager);
        this.notificationManager = notificationManager;
        this.notificationSender = notificationSender;
        this.transactionManager = transactionManager;
    }

    void isUserInRole() {

    }

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException {
        isUserInRole();
        try {
            List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
            criteria.add(new In(NotificationCriteriaFields.TYPE,
                    new HashSet(Arrays.asList(NotificationVO.Type.CONTACT_CONFIRMATION,
                            NotificationVO.Type.USDOC_CONFIRMATION))));
            return NotificationConverter.toNotificationVOList(notificationManager.find(
                    convertNotificationCriteria(new And(criteria))));
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    public List<NotificationVO> getNotifications(Criterion criteria) throws InfrastructureException {
        isUserInRole();
        try {
            return NotificationConverter.toNotificationVOList(notificationManager.find(
                    convertNotificationCriteria(criteria)));
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    public void resendNotification(Set<NotificationAddresseeVO> addressees, long notificationId, String comment) throws InfrastructureException{
        isUserInRole();
        try {
            Notification notification = notificationManager.get(notificationId);
            notificationSender.send(NotificationConverter.toAddresseeSet(addressees),
                    notification.getContent().getSubject(),
                    comment(notification.getContent().getBody(), comment));
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment,  String email) throws InfrastructureException, FacadeTransactionException {
        isUserInRole();
        try {
            if (type == NotificationVO.Type.CONTACT_CONFIRMATION) {
                Transaction transaction = transactionManager.getTransaction(transactionId);

                Set<ContactIdentity> identities = transaction.getIdentitiesSupposedToAccept();
                Set<String> outstendingEmails = extractEmails(identities);

                List<Criterion> criteria = new ArrayList<Criterion>();
                criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
                criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
                List<Notification> notifications = notificationManager.find(convertNotificationCriteria(new And(criteria)));

                for (Notification notif : notifications) {
                    Set<String> addresseeEmails = extractEmails(notif.getAddressee());
                    if (!Collections.disjoint(addresseeEmails, outstendingEmails)) {
                        notificationSender.send(notif);
                    }
                }
            } else if (type == NotificationVO.Type.USDOC_CONFIRMATION) {
                Transaction transaction = transactionManager.getTransaction(transactionId);
                Confirmation sc = transaction.getTransactionData().getStateConfirmations(TransactionState.Name.PENDING_USDOC_APPROVAL.name());
                if (!sc.isReceived()) {
                    List<Criterion> criteria = new ArrayList<Criterion>();
                    criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
                    criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
                    List<Notification> notifications = notificationManager.find(convertNotificationCriteria(new And(criteria)));
                    for (Notification notif : notifications) {
                        notificationSender.send(notif);
                    }
                }
            } else {
                throw new FacadeTransactionException("unsupported notification type: " + type);
            }
        } catch (NoSuchTransactionException e) {
            throw new InfrastructureException(e);
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    private Set<String> extractEmails(Set<? extends Addressee> addressees) {
        Set<String> result = new HashSet<String>();
        for (Addressee addr : addressees)
            result.add(addr.getEmail());
        return result;
    }

    private String comment(String body, String comment) {
        StringBuffer buffer = new StringBuffer();
        if (comment != null)
            buffer.append(comment).append("\n");
        buffer.append(body);
        return buffer.toString();
    }

    private Criterion convertNotificationCriteria(Criterion criteria) {
        criteria.accept(new NotificationCriteriaConverter());
        return criteria;
    }
}
