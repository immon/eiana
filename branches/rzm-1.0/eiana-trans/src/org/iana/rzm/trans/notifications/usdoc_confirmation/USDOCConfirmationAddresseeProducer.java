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
    private String nsChangeTemplateName;

    public USDOCConfirmationAddresseeProducer(UserManager userManager, String nsChangeTemplateName) {
        this(userManager, new HashMap<String, String>(), nsChangeTemplateName);
    }

    public USDOCConfirmationAddresseeProducer(UserManager userManager, Map<String, String>ccEmails, String nsChangeTemplateName) {
        CheckTool.checkNull(userManager, "user manager is null");
        this.ccEmails = ccEmails;
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


        for (String name : ccEmails.keySet()) {
            String email = ccEmails.get(name);
            if(email != null){
                addressees.add(new PAddressee(name, email, true));
            }
        }

        return addressees;
    }
}
