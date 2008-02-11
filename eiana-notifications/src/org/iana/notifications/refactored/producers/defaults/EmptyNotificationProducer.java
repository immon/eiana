package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.producers.NotificationProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class EmptyNotificationProducer implements NotificationProducer {

    public List<PNotification> produce(Map dataSource) {
        return new ArrayList<PNotification>();
    }
}
