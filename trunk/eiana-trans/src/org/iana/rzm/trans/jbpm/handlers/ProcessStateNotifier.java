package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.user.RZMUser;
import org.iana.notifications.Addressee;
import org.iana.notifications.dao.NotificationDAO;
import org.iana.notifications.dao.HibernateNotificationDAO;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class ProcessStateNotifier implements ActionHandler {

    TransactionData td;
    private   NotificationSender   notificationSender;
    protected NotificationTemplate notificationTemplate;
    protected String               notification;
    protected NotificationManager  notificationManagerBean;
    
    public void execute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
    }

    protected void fillDataFromContext(ExecutionContext executionContext) throws NotificationException {
        td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
        notificationTemplate = NotificationTemplateManager.getInstance().getNotificationTemplate(notification);
    }

    protected void sendContactNotification(RZMUser user, Object template) throws Exception {
        Notification notification = notificationTemplate.getNotificationInstance(template);
        notification.addAddressee(user);
        try {
            notificationSender.send(notification.getAddressee(), notification.getContent());
        } catch(NotificationException e) {
            notification.incSentFailures();
            notificationManagerBean .create(notification);
        }
    }
}
