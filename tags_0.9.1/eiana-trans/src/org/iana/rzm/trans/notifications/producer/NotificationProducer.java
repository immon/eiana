package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.Notification;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public interface NotificationProducer {

    List<Notification> produce(Map<String, Object> dataSource);

}
