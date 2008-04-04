package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.notifications.PAddressee;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class USDOCConfirmationAddresseeProducer extends AbstractTransactionAddresseeProducer {

    private UserManager userManager;

    public USDOCConfirmationAddresseeProducer(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager is null");
        this.userManager = userManager;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        if (td.isNameServerChange()) {
            users.addAll(userManager.findUsersInAdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        }

        Set<PAddressee> addressees = new HashSet<PAddressee>(users.size());
        for (RZMUser user : users) {
            boolean isZonePublisher = user.isInRole(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER), roleComparator);
            addressees.add(new PAddressee(user.getName(), user.getEmail(), isZonePublisher));
        }
        return addressees;
    }
}
