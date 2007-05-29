package org.iana.rzm.trans.confirmation.contact;

import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.trans.jbpm.handlers.ProcessStateNotifier;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.notifications.Notification;
import org.iana.notifications.TemplateContent;
import org.iana.notifications.EmailAddressee;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationNotifier extends ProcessStateNotifier {

    public List<Notification> getNotifications() {
        List<Notification> ret = new ArrayList<Notification>();
        String domainName = td.getCurrentDomain().getName();
        ContactConfirmations conf = td.getContactConfirmations();
        for(Identity identity : conf.getUsersAbleToAccept()) {
            ContactIdentity contact = (ContactIdentity) identity;

            Map<String, String> values = new HashMap<String, String>();
            values.put("roleName", contact.getType().toString());
            values.put("domainName", domainName);
            values.put("mustAccept", "are allowed to");
            values.put("transactionId", "" + transactionId);
            values.put("stateName", stateName);
            values.put("token", contact.getToken());

            TemplateContent templateContent = new TemplateContent(notification, values);
            Notification notification = new Notification();
            notification.addAddressee(new EmailAddressee(contact.getEmail(), contact.getName()));
            notification.setContent(templateContent);

            ret.add(notification);
        }
        return ret;
    }
}
