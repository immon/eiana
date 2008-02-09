package org.iana.rzm.trans.notifications.zone_insertion;

import org.iana.notifications.Addressee;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ZoneInsertionAlertAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<Addressee> produceAddressee(Map dataSource) {
        Set<Addressee> addressees = new HashSet<Addressee>();
        addressees.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());
        return addressees;
    }
}
