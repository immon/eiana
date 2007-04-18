package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.template.TemplateContactConfirmationRemainder;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

public class ContactConfirmationRemainder extends ProcessStateNotifier implements ActionHandler {

    int period;
    
    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
        String domainName = td.getCurrentDomain().getName();

        Set<RZMUser> users = new HashSet<RZMUser>();
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());      

        for(RZMUser user : users)
            for (Role role : user.getRoles()) {
                SystemRole systemRole = (SystemRole) role;
                TemplateContactConfirmationRemainder template = new TemplateContactConfirmationRemainder(domainName,
                        systemRole.getTypeName(), systemRole.isMustAccept(), 30-period);
                sendContactNotification(user, template);
            }
    }
}
