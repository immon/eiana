package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.TemplateContent;
import org.iana.notifications.Notification;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.auth.Identity;

import java.util.*;

/**
 * This class notifies all required contacts that a domain transaction needs to be confirmed.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class ContactNotifier extends ProcessStateNotifier {

      public List<Notification> getNotifications() {

        String domainName = td.getCurrentDomain().getName();

        Set<Identity> users = new HashSet<Identity>();
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());

        List<Notification> notifications = new ArrayList<Notification>();

        for(Identity identity : users) {
            RZMUser user = (RZMUser) identity;
            for (Role role : user.getRoles()) {
                SystemRole systemRole = (SystemRole) role;

                Map<String, String> values = new HashMap<String, String>();
                values.put("roleName", systemRole.getTypeName());
                values.put("domainName", domainName);
                values.put("mustAccept", (systemRole.isMustAccept())? "must" : "are allowed to");
                values.put("transactionId", "" + transactionId);
                values.put("stateName", stateName);

                TemplateContent templateContent = new TemplateContent(notification, values);
                Notification notification = new Notification();
                notification.addAddressee(user);
                notification.setContent(templateContent);

                notifications.add(notification);
            }
        }
        return notifications;
    }
}
