package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.notifications.PAddressee;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class USDOCConfirmationAddresseeProducer extends AbstractTransactionAddresseeProducer {

    private UserManager userManager;
    private Map<String, String> ccEmails;

    public USDOCConfirmationAddresseeProducer(UserManager userManager) {
        this(userManager, new HashMap<String, String>());
    }

    public USDOCConfirmationAddresseeProducer(UserManager userManager, Map<String, String>ccEmails) {
        CheckTool.checkNull(userManager, "user manager is null");
        this.ccEmails = ccEmails;
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


        for (String name : ccEmails.keySet()) {
            addressees.add(new PAddressee(name, ccEmails.get(name), true));    
        }

        return addressees;
    }
}
