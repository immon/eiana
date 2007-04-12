package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.trans.confirmation.SystemRoleConfirmation;
import org.iana.notifications.template.TemplateContactConfirmation;

import java.util.*;

/**
 * This class notifies all required contacts that a domain transaction needs to be confirmed.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class ContactNotifier extends ProcessStateNotifier implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);

        String domainName = td.getCurrentDomain().getName();

        Set<RZMUser> users = new HashSet<RZMUser>();
        users.addAll(new SystemRoleConfirmation(domainName, SystemRole.SystemType.AC).getUsersAbleToAccept());
        users.addAll(new SystemRoleConfirmation(domainName, SystemRole.SystemType.TC).getUsersAbleToAccept());

        for(RZMUser user : users)
            for (Role role : user.getRoles()) {
                TemplateContactConfirmation template = new TemplateContactConfirmation(domainName, ((SystemRole)role).getTypeName(), user.mustAccept(domainName, role.getType()));
                sendContactNotification(user, template);
            }
    }
}
