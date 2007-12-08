package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.notification.NotificationAddresseeVO;
import org.iana.rzm.facade.system.notification.NotificationConverter;
import org.iana.rzm.facade.system.notification.NotificationCriteriaConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.UserManager;

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
            List<Notification> found = getSavedNotifications(transactionId);
            return NotificationConverter.toNotificationVOList(found);
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    private List<Notification> getSavedNotifications(long transactionId) throws NotificationException {
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
        criteria.add(new Equal(NotificationCriteriaFields.PERSISTENT, Boolean.TRUE));
        Criterion notificationsForTrans = new And(criteria);
        return notificationManager.find(notificationsForTrans);
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

    public void resendNotification(Set<NotificationAddresseeVO> addressees, long notificationId, String comment) throws InfrastructureException {
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

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment) throws InfrastructureException, FacadeTransactionException {
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
                        resend(notif, comment);
                    }
                }
            } else if (type == NotificationVO.Type.USDOC_CONFIRMATION) {
                List<Notification> notifications = findUSDoCNotifications(transactionId);
                for (Notification notif : notifications) {
                    resend(notif, comment);
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

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment, String addresseeEmail) throws InfrastructureException, FacadeTransactionException {
        isUserInRole();
        if (addresseeEmail == null) {
            resendNotification(transactionId, type, comment);
            return;
        }
        try {
            Set<String> addressee = new HashSet<String>();
            addressee.add(addresseeEmail);

            if (type == NotificationVO.Type.CONTACT_CONFIRMATION) {
                List<Criterion> criteria = new ArrayList<Criterion>();
                criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
                criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
                List<Notification> notifications = notificationManager.find(convertNotificationCriteria(new And(criteria)));
                for (Notification notif : notifications) {
                    resend(notif, addressee, comment);
                }
            } else if (type == NotificationVO.Type.USDOC_CONFIRMATION) {
                List<Notification> notifications = findUSDoCNotifications(transactionId);
                for (Notification notif : notifications) {
                    resend(notif, addressee, comment);
                }
            } else {
                throw new FacadeTransactionException("unsupported notification type: " + type);
            }
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    private List<Notification> findUSDoCNotifications(long transactionID) throws NotificationException {
        List<Notification> ret = new ArrayList<Notification>();
        for (Notification notif : getSavedNotifications(transactionID)) {
            if (NotificationConverter.isType(notif.getType(), NotificationVO.Type.USDOC_CONFIRMATION)) {
                ret.add(notif);
            }
        }
        return ret;
    }

    private Set<String> extractEmails(Set<? extends Addressee> addressees) {
        Set<String> result = new HashSet<String>();

        for (Addressee addr : addressees){
            if(addr.getEmail() != null){
                result.add(addr.getEmail());
            }
        }
        
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

    private void resend(Notification notification, Set<String> set, String comment) throws NotificationException {
        Set<Addressee> addressees = new HashSet<Addressee>();
        for (String email : set) addressees.add(new EmailAddressee(email, email));
        notificationSender.send(addressees, notification.getContent().getSubject(), comment(notification.getContent().getBody(), comment));
    }

    private void resend(Notification notification, String comment) throws NotificationException {
        notificationSender.send(notification.getAddressee(), notification.getContent().getSubject(), comment(notification.getContent().getBody(), comment));
    }
}
