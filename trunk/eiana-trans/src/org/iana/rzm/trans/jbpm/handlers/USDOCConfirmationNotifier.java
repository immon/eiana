package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;
import org.iana.notifications.TemplateContent;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jakub Laszkiewicz
 */

public class USDOCConfirmationNotifier extends ProcessStateNotifier {

    protected String period;

    public List<Notification> getNotifications() {
        String domainName = td.getCurrentDomain().getName();
        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        List<Notification> notifications = new ArrayList<Notification>();

        for (RZMUser user : users) {
            Map<String, String> values = new HashMap<String, String>();
            values.put("domainName", domainName);
            values.put("transactionId", "" + transactionId);
            values.put("stateName", stateName);
            TemplateContent templateContent = new TemplateContent(notification, values);

            Notification notification = new Notification(transactionId);
            notification.addAddressee(user);
            notification.setContent(templateContent);
            notification.setPersistent(true);
            notifications.add(notification);
        }
        return notifications;
    }
}
