package org.iana.notifications.refactored.producers;

import org.iana.notifications.refactored.PNotification;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface NotificationProducer {

    List<PNotification> produce(Map<String, Object> dataSource) throws NotificationProducerException;

}
