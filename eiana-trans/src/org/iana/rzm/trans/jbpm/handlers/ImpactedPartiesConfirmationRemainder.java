package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class ImpactedPartiesConfirmationRemainder extends ProcessStateNotifier {

    protected String period;

    public List<Notification> getNotifications() {

        List<Notification> notifications = new ArrayList<Notification>();

        //todo
        return notifications;
    }
}
