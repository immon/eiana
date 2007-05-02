package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;

import java.util.List;
import java.util.ArrayList;


/**
 * @author: Piotr Tkaczyk
 */

public class USDOCConfirmationRemainder extends ProcessStateNotifier {

    protected String period;

    public List<Notification> getNotifications() {
        //todo
        return new ArrayList<Notification>();
    }
}
