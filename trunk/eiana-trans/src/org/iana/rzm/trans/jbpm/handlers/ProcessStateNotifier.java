package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.trans.TransactionData;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class ProcessStateNotifier implements ActionHandler {

    TransactionData td;
    protected NotificationSender notificationSender;
    protected NotificationTemplate notificationTemplate;
    protected String notification;
    
    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
    }

    protected void sendNotification(Addressee addressee, Notification notif) throws NotificationException {
        notificationSender.send(addressee, notif.getContent());
    }
    
    protected void sendNotification(Collection<Addressee> addressees, Notification notif) throws NotificationException {
        notificationSender.send(addressees, notif.getContent());
    }

    protected void fillDataFromContext(ExecutionContext executionContext) throws NotificationException {
        td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
        notificationTemplate = NotificationTemplateManager.getInstance().getNotificationTemplate(notification);
    }
}
