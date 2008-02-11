package org.iana.notifications.refactored.producers;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface DataProducer {
    public Map<String, String> getValuesMap(Map dataSource);
}
