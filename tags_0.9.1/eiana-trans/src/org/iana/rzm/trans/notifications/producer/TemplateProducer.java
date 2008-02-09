package org.iana.rzm.trans.notifications.producer;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface TemplateProducer {
    List<String> produce(Map dataSource);
}
