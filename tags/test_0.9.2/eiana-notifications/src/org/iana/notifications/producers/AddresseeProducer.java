package org.iana.notifications.producers;

import org.iana.notifications.PAddressee;

import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface AddresseeProducer {

    Set<PAddressee> produce(Map dataSource);

}
