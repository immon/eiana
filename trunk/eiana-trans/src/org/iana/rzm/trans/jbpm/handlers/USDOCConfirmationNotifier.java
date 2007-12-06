package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Content;
import org.iana.notifications.Notification;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.trans.change.DomainChangePrinter;

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
        if (td.isNameServerChange()) {
            users.addAll(userManager.findUsersInAdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        }
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
            values.put("change", DomainChangePrinter.print(td.getDomainChange()));

            List<String> notifs = getNotificationTypes();
            for (String notif : notifs) {
                Content templateContent = templateContentFactory.createContent(notif, values);

                Notification notification = new Notification(transactionId);
                notification.addAddressee(user);
                notification.setContent(templateContent);
                notification.setPersistent(true);
                notifications.add(notification);
            }
        }
        return notifications;
    }

    private List<String> getNotificationTypes() {
        List<String> ret = new ArrayList<String>();
        if (td.isNameServerChange()) ret.add("usdoc-confirmation-nschange");
        if (td.isDatabaseChange()) ret.add("usdoc-confirmation");
        return ret;
    }

}
