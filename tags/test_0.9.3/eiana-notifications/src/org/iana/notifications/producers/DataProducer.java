package org.iana.notifications.producers;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface DataProducer {
    public Map<String, String> getValuesMap(Map dataSource);
}
