package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.producers.AddresseeProducer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultAddresseeProducer implements AddresseeProducer {

    public static final String ADDRESSEE = "addressee";

    public static final String ADDRESSEES = "addressees";

    @SuppressWarnings("unchecked")
    public Set<PAddressee> produce(Map dataSource) {
        Object addressees = dataSource.get(ADDRESSEES);
        if (addressees != null) {
            return (Set<PAddressee>) addressees;
        }
        Object addressee = dataSource.get(ADDRESSEE);
        if (addressee != null) {
            Set<PAddressee> ret = new HashSet<PAddressee>();
            ret.add((PAddressee) addressee);
            return ret;
        }
        throw new IllegalStateException("cannot find addressee(s) in data source");
    }

}
