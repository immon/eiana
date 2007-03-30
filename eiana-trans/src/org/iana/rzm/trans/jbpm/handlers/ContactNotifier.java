package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.configuration.ObjectFactory;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.Role;
import org.iana.notifications.*;
import org.iana.notifications.template.TemplateContactConfirmation;

import java.util.*;

/**
 * This class notifies all required contacts that a domain transaction needs to be confirmed.
 *
 * @author Patrycja Wegrzynowicz
 */
public class ContactNotifier extends ProcessStateNotifier implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);

        Set<RZMUser> users = getAllUsersInRole(executionContext, td.getCurrentDomain().getName());

        for(RZMUser user : users)
            sendContactNotification(td.getCurrentDomain().getName(), user);
    }

    private void sendContactNotification(String domainName, RZMUser user) throws Exception {
        for (Role role : user.getRoles()) {
            if (((SystemRole)role).isNotify()) {
                TemplateContactConfirmation tCC = new TemplateContactConfirmation(domainName, ((SystemRole)role).getFullTypeName(), user.mustAccept(domainName, role.getType()));
                Notification notification = notificationTemplate.getNotificationInstance(tCC);
                String userFullName = user.getFirstName() + " " + user.getLastName();
                sendNotification(new EmailAddress(userFullName, user.getEmail()), notification);
            }
        }
    }

    private Set<RZMUser> getAllUsersInRole(ExecutionContext executionContext, String domainName) {
        ObjectFactory of = executionContext.getJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        Set<RZMUser> users = new HashSet<RZMUser>();
        users.addAll(um.findUsersRequiredToConfirm(domainName, SystemRole.SystemType.AC));
        users.addAll(um.findUsersRequiredToConfirm(domainName, SystemRole.SystemType.TC));
        users.addAll(um.findUsersEligibleToConfirm(domainName, SystemRole.SystemType.AC));
        users.addAll(um.findUsersEligibleToConfirm(domainName, SystemRole.SystemType.TC));
        return users;
    }
}
