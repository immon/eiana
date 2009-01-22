package org.iana.notifications.producers;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface TemplateNameProducer {
    List<String> produce(Map dataSource);
}
