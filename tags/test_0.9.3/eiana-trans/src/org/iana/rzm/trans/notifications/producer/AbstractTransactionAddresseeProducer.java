package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.Role;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractTransactionAddresseeProducer implements AddresseeProducer {

    private boolean sendToSubmitter = false;

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        if (sendToSubmitter) {
            TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
            String submitterEmail = td.getSubmitterEmail();
            if (CheckTool.isCorrectEmali(submitterEmail))
                addressees.add(new PAddressee(submitterEmail, submitterEmail));
        }
        addressees.addAll(produceAddressee(dataSource));
        return addressees;
    }

    public boolean isSendToSubmitter() {
        return sendToSubmitter;
    }

    public void setSendToSubmitter(boolean sendToSubmitter) {
        this.sendToSubmitter = sendToSubmitter;
    }

    protected Set<PAddressee> getAddressees(AdminRole.AdminType adminType) {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        Set<PAddressee> ret = new HashSet<PAddressee>();
        for (RZMUser user : um.findUsersInAdminRole(adminType)) {
            ret.add(new PAddressee(user.getName(), user.getEmail()));
        }
        return ret;
    }

    abstract protected Set<PAddressee> produceAddressee(Map dataSource);

    protected static RoleComparator roleComparator = new RoleComparator();

    public static class RoleComparator implements Comparator<Role> {
        public int compare(Role o1, Role o2) {
            if (o1.getClass() != o2.getClass()) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
            return o1.getType().toString().compareTo(o2.getType().toString());
        }
    }
}
