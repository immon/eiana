package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Content;
import org.iana.notifications.Notification;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

public class USDOCConfirmationRemainder extends ProcessStateNotifier {

    protected String period;

    public List<Notification> getNotifications() {
        String domainName = td.getCurrentDomain().getName();
        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        List<Notification> notifications = new ArrayList<Notification>();

        for (RZMUser user : users) {
            Map<String, String> values = new HashMap<String, String>();
            values.put("domainName", domainName);
            values.put("period", period);
            values.put("transactionId", "" + transactionId);
            values.put("stateName", stateName);
            Content templateContent = transactionTemplateContentFactory.createContent(notification, values, td);

            Notification notification = new Notification(transactionId, td.getTicketID());
            notification.addAddressee(user);
            notification.setContent(templateContent);
            notifications.add(notification);
        }
        return notifications;
    }
}
