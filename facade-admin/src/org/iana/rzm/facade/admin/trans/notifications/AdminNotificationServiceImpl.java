package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.notifications.refactored.NotificationSender;
import org.iana.notifications.refactored.NotificationSenderException;
import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.PNotification;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.notification.NotificationConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.UserManager;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class AdminNotificationServiceImpl extends AbstractRZMStatefulService implements AdminNotificationService {

    NotificationSender notificationSender;

    TransactionManager transactionManager;

    public AdminNotificationServiceImpl(UserManager userManager, NotificationSender notificationSender, TransactionManager transactionManager) {
        super(userManager);
        this.notificationSender = notificationSender;
        this.transactionManager = transactionManager;
    }

    void isUserInRole() {

    }

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException {
        isUserInRole();
        try {
            Transaction trans = transactionManager.getTransaction(transactionId);
            Set<PNotification> saved = trans.getNotifications();
            return NotificationConverter.toNotificationVOList(saved);
        } catch (NoSuchTransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment) throws InfrastructureException, FacadeTransactionException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(transactionId);
            List<PNotification> notifications = findNotifications(transaction, type);
            if (type == NotificationVO.Type.CONTACT_CONFIRMATION) {
                Set<ContactIdentity> identities = transaction.getIdentitiesSupposedToAccept();
                Set<String> outstendingEmails = new HashSet<String>();
                for (ContactIdentity identity : identities) {
                    outstendingEmails.add(identity.getEmail());
                }
                for (PNotification notif : notifications) {
                    Set<String> addresseeEmails = extractEmails(notif.getAddressees());
                    if (!Collections.disjoint(addresseeEmails, outstendingEmails)) {
                        resend(notif, comment);
                    }
                }
            } else if (type == NotificationVO.Type.USDOC_CONFIRMATION) {
                for (PNotification notif : notifications) {
                    resend(notif, comment);
                }
            } else {
                throw new FacadeTransactionException("unsupported notification type: " + type);
            }
        } catch (NoSuchTransactionException e) {
            throw new InfrastructureException(e);
        } catch (NotificationSenderException e) {
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
            if (type == NotificationVO.Type.CONTACT_CONFIRMATION ||
                    type == NotificationVO.Type.USDOC_CONFIRMATION) {
                Transaction trans = transactionManager.getTransaction(transactionId);
                List<PNotification> notifs = findNotifications(trans, type);
                for (PNotification notif : notifs) {
                    resend(notif, addressee, comment);
                }
            } else {
                throw new FacadeTransactionException("unsupported notification type: " + type);
            }
        } catch (NotificationSenderException e) {
            throw new InfrastructureException(e);
        } catch (NoSuchTransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    private List<PNotification> findNotifications(Transaction trans, NotificationVO.Type voType) {
        List<PNotification> ret = new ArrayList<PNotification>();
        for (PNotification notif : trans.getNotifications()) {
            if (NotificationConverter.isType(notif.getType(), voType)) {
                ret.add(notif);
            }
        }
        return ret;
    }

    private Set<String> extractEmails(Set<PAddressee> addressees) {
        Set<String> result = new HashSet<String>();
        for (PAddressee addr : addressees) {
            if (addr.getEmail() != null) {
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

    private void resend(PNotification notification, Set<String> set, String comment) throws NotificationSenderException {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        for (String email : set) addressees.add(new PAddressee(email, email));
        PNotification tosend = new PNotification(addressees, notification.getContent().getSubject(), comment(notification.getContent().getBody(), comment));
        notificationSender.send(tosend);
    }

    private void resend(PNotification notification, String comment) throws NotificationSenderException {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        for (PAddressee addr : notification.getAddressees()) {
            addressees.add(new PAddressee(addr.getName(), addr.getEmail()));
        }
        PNotification tosend = new PNotification(addressees, notification.getContent().getSubject(), comment(notification.getContent().getBody(), comment));
        notificationSender.send(tosend);
    }
}
