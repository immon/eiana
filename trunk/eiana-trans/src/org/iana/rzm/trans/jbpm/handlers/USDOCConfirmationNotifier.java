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
 * @author Jakub Laszkiewicz
 */
public class USDOCConfirmationNotifier extends ProcessStateNotifier {

    protected String eppID;

    protected String receipt;

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public void setEppID(String eppID) {
        this.eppID = eppID;
    }

    public List<Notification> getNotifications() {
        String domainName = td.getCurrentDomain().getName();
        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        users.addAll(userManager.findUsersInAdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        List<Notification> notifications = new ArrayList<Notification>();

        for (RZMUser user : users) {
            Map<String, String> values = new HashMap<String, String>();
            values.put("domainName", domainName);
            values.put("transactionId", "" + transactionId);
            values.put("stateName", stateName);
            values.put("receipt", receipt);
            values.put("ticket", "" + td.getTicketID());
            values.put("notes", "");
            values.put("eppid", eppID);
            Content templateContent = templateContentFactory.createContent(notification, values);

            Notification notification = new Notification(transactionId);
            notification.addAddressee(user);
            notification.setContent(templateContent);
            notification.setPersistent(true);
            notifications.add(notification);
        }
        return notifications;
    }
}
