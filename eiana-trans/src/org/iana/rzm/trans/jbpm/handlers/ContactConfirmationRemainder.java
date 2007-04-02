/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.configuration.ObjectFactory;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.notifications.template.TemplateContactConfirmationRemainder;
import org.iana.notifications.Notification;
import org.iana.notifications.EmailAddress;

import java.util.Set;
import java.util.HashSet;

public class ContactConfirmationRemainder extends ProcessStateNotifier implements ActionHandler {

    int period;
    
    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);

        Set<RZMUser> users = getAllUsersInRole(executionContext, td.getCurrentDomain().getName());

        for(RZMUser user : users)
            sendContactNotification(td.getCurrentDomain().getName(), user);
    }

    private void sendContactNotification(String domainName, RZMUser user) throws Exception {
        for (Role role : user.getRoles()) {
            if (((SystemRole)role).isNotify()) {
                TemplateContactConfirmationRemainder tCCR = new TemplateContactConfirmationRemainder(domainName, ((SystemRole)role).getTypeName(), user.mustAccept(domainName, role.getType()), 30-period);
                Notification notification = notificationTemplate.getNotificationInstance(tCCR);
                String userFullName = user.getFirstName() + " " + user.getLastName();
                sendNotification(new EmailAddress(userFullName, user.getEmail()), notification);
            }
        }
    }

    private Set<RZMUser> getAllUsersInRole(ExecutionContext executionContext, String domainName) {
        ObjectFactory of = executionContext.getJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        Set<RZMUser> users = new HashSet<RZMUser>();
        users.addAll(um.findUsersInSystemRole(domainName, SystemRole.SystemType.AC, true, true));
        users.addAll(um.findUsersInSystemRole(domainName, SystemRole.SystemType.TC, true, true));
        users.addAll(um.findUsersInSystemRole(domainName, SystemRole.SystemType.AC, true, false));
        users.addAll(um.findUsersInSystemRole(domainName, SystemRole.SystemType.TC, true, false));
        return users;
    }
}
