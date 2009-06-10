package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminRecipients implements AddresseeProducer {

    private AdminRole.AdminType adminType;

    public AdminRecipients(AdminRole.AdminType adminType) {
        CheckTool.checkNull(adminType, "admin role.type");
        this.adminType = adminType;
    }

    public Set<PAddressee> produce(Map dataSource) {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        Set<PAddressee> ret = new HashSet<PAddressee>();
        for (RZMUser user : um.findUsersInAdminRole(adminType)) {
            ret.add(new PAddressee(user.getName(), user.getEmail()));
        }
        return ret;
    }

}
