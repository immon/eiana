package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.Addressee;

import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface AddresseeProducer {

    Set<Addressee> produce(Map dataSource);

}
