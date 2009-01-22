package org.iana.notifications.producers;

import org.iana.notifications.PNotification;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface NotificationProducer {

    List<PNotification> produce(Map<String, Object> dataSource) throws NotificationProducerException;

}
