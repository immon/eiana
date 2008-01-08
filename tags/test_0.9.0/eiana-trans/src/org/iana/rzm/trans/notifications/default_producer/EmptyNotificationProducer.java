package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.Notification;
import org.iana.rzm.trans.notifications.producer.NotificationProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class EmptyNotificationProducer implements NotificationProducer {

    public List<Notification> produce(Map dataSource) {
        return new ArrayList<Notification>();
    }
}
