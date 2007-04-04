/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.trans.confirmation.SystemRoleConfirmation;
import org.iana.notifications.template.TemplateContactConfirmationRemainder;

import java.util.Set;
import java.util.HashSet;

public class ContactConfirmationRemainder extends ProcessStateNotifier implements ActionHandler {

    int period;
    
    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
        String domainName = td.getCurrentDomain().getName();

        Set<RZMUser> users = new HashSet<RZMUser>();
        users.addAll(new SystemRoleConfirmation(domainName, SystemRole.SystemType.AC).getUsersAbleToAccept());
        users.addAll(new SystemRoleConfirmation(domainName, SystemRole.SystemType.TC).getUsersAbleToAccept());      

        for(RZMUser user : users)
            for (Role role : user.getRoles()) {
                TemplateContactConfirmationRemainder template = new TemplateContactConfirmationRemainder(domainName, ((SystemRole)role).getTypeName(), user.mustAccept(domainName, role.getType()), 30-period);
                sendContactNotification(domainName, user, template);
            }
    }
}
