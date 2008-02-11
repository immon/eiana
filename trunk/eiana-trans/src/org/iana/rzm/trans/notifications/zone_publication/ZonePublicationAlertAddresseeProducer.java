package org.iana.rzm.trans.notifications.zone_publication;

import org.iana.notifications.refactored.PAddressee;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ZonePublicationAlertAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        addressees.addAll(getAddressees(AdminRole.AdminType.IANA));
        return addressees;
    }
}
