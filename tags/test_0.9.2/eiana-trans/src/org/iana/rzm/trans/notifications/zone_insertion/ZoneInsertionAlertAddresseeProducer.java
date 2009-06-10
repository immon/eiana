package org.iana.rzm.trans.notifications.zone_insertion;

import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ZoneInsertionAlertAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        addressees.addAll(getAddressees(AdminRole.AdminType.IANA));
        return addressees;
    }
}
