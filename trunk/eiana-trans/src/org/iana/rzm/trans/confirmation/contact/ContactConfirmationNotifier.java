package org.iana.rzm.trans.confirmation.contact;

import org.iana.notifications.Content;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.Notification;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.trans.collectors.ContactNotificationDataCollector;
import org.iana.rzm.trans.collectors.NotificationDataCollector;
import org.iana.rzm.trans.jbpm.handlers.ProcessStateNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class ContactConfirmationNotifier extends ProcessStateNotifier {

    public List<Notification> getNotifications() {
        List<Notification> ret = new ArrayList<Notification>();
        ContactConfirmations conf = td.getContactConfirmations();
        for (Identity identity : conf.getUsersAbleToAccept()) {
            ContactIdentity contact = (ContactIdentity) identity;

            NotificationDataCollector notifiData = new ContactNotificationDataCollector(td, contact, transactionId, stateName);

            Content templateContent = templateContentFactory.createContent(notification, notifiData.getValuesMap());
            Notification notification = new Notification(transactionId);
            notification.addAddressee(new EmailAddressee(contact.getEmail(), contact.getName()));
            if (td.getSubmitterEmail() != null)
                notification.addAddressee(new EmailAddressee(td.getSubmitterEmail(), td.getSubmitterEmail()));
            notification.setContent(templateContent);
            notification.setPersistent(true);

            ret.add(notification);
        }
        return ret;
    }
}
