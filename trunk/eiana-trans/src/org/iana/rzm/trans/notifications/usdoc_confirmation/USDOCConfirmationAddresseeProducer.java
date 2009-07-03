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
    private String nsChangeTemplateName;

    public USDOCConfirmationAddresseeProducer(UserManager userManager, String nsChangeTemplateName) {
        CheckTool.checkNull(userManager, "user manager is null");
        this.userManager = userManager;
        this.nsChangeTemplateName = nsChangeTemplateName;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {

        String templateName = (String) dataSource.get(USDOCConfirmationNotificationProducer.TEMPLATE_NAME);

        List<RZMUser> users = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        if (nsChangeTemplateName.equals(templateName)) {
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
