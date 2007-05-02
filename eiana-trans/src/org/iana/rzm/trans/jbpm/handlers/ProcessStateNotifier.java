package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.notifications.Addressee;

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
        sendNotifications(getNotifications());
    }

    private void fillDataFromContext(ExecutionContext executionContext) throws NotificationException {
        td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
        notificationTemplate = NotificationTemplateManager.getInstance().getNotificationTemplate(notification);
    }

    private void sendNotifications(List<Notification> notifications) throws Exception {
        for (Notification notification: notifications)
            sendNotification(notification);
    }

    private void sendNotification(Notification notification) throws Exception {
        try {
            if (!notification.getAddressee().isEmpty())
                notificationSender.send(notification.getAddressee(), notification.getContent());
        } catch(NotificationException e) {
            notification.incSentFailures();
            notificationManagerBean.create(notification);
        }
    }

    public List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();

        String domainName = td.getCurrentDomain().getName();
        Set<Addressee> users = new HashSet<Addressee>();

        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.SO, domainName, true, false)).getUsersAbleToAccept());

        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER)).getUsersAbleToAccept());

        TemplateContent templateContent = new TemplateContent(notification, new HashMap<String,String>());
        Notification notification = new Notification();
        notification.setContent(templateContent);
        notification.setAddressee(users);
        notifications.add(notification);

        return notifications ;
    }
}
