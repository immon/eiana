package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.Notification;
import org.iana.notifications.TemplateContent;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.AdminRole;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class ZoneInsertionAlert extends ProcessStateNotifier {

    protected String period;

    public List<Notification> getNotifications() {

        List<Notification> notifications = new ArrayList<Notification>();

        String domainName = td.getCurrentDomain().getName();
        Set<Addressee> users = new HashSet<Addressee>();

        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());

        Map<String, String> values = new HashMap<String, String>();
        values.put("domainName", domainName);
        values.put("period", period);
        TemplateContent templateContent = new TemplateContent(notification, values);
        Notification notification = new Notification();
        notification.setContent(templateContent);
        notification.setAddressee(users);
        if (td.getSubmitterEmail() != null)
            notification.addAddressee(new EmailAddressee(td.getSubmitterEmail(), td.getSubmitterEmail()));
        notifications.add(notification);

        return notifications;
    }
}
