package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.rzm.common.validators.CheckTool;
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
public class USDOCConfirmationRemainderAddresseeProducer extends AbstractTransactionAddresseeProducer {

    private UserManager userManager;

    public USDOCConfirmationRemainderAddresseeProducer(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager is null");
        this.userManager = userManager;
    }

    public Set<Addressee> produceAddressee(Map dataSource) {
        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);

        Set<Addressee> addressees = new HashSet<Addressee>();
        for (RZMUser user : users)
            addressees.add(new EmailAddressee(user.getEmail(), user.getName()));

        return addressees;
    }
}
