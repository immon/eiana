package org.iana.notifications.refactored.producers;

import org.iana.notifications.refactored.PAddressee;

import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface AddresseeProducer {

    Set<PAddressee> produce(Map dataSource);

}
