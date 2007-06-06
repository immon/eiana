package org.iana.rzm.trans.collectors;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface NotificationDataCollector {

    //Generates map of values for TemplateContent
    public Map<String, String> getValuesMap();
}
