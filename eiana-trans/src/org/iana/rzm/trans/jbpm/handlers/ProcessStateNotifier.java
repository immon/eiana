package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class ProcessStateNotifier extends ActionExceptionHandler {

    protected TransactionData td;
    private NotificationSender notificationSender;
    protected NotificationTemplate notificationTemplate;
    protected String notification;
    protected NotificationManager notificationManagerBean;
    protected UserManager userManager;
    protected Long transactionId;
    protected String stateName;
    protected List<String> emails;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
        if (!td.getCurrentDomain().isEnableEmails()) return;
        sendNotifications(getNotifications());
    }

    private void fillDataFromContext(ExecutionContext executionContext) throws NotificationException {
        td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("persistentNotificationSender");
        notificationTemplate = NotificationTemplateManager.getInstance().getNotificationTemplate(notification);
        transactionId = executionContext.getProcessInstance().getId();
        stateName = executionContext.getProcessInstance().getRootToken().getNode().getName();
        userManager = (UserManager) executionContext.getJbpmContext().getObjectFactory().createObject("userManager");
    }

    private void sendNotifications(List<Notification> notifications) throws Exception {
        for (Notification notification : notifications)
            sendNotification(notification);
    }

    private void sendNotification(Notification notification) throws Exception {
        if (!notification.getAddressee().isEmpty())
            notificationSender.send(notification);
    }

    public List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();

        String domainName = td.getCurrentDomain().getName();
        Set<Addressee> users = new HashSet<Addressee>();

        // bug: selection: all users in any role for a given domain name!
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.SO, domainName, true, false)).getUsersAbleToAccept());

        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());
        users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER)).getUsersAbleToAccept());

        if (emails != null && !emails.isEmpty())
            for (String email : emails)
                users.add(new EmailAddressee(email, email));

        TemplateContent templateContent = new TemplateContent(notification, new HashMap<String, String>());
        Notification notification = new Notification(transactionId);
        notification.setContent(templateContent);
        notification.setAddressee(users);
        if (td.getSubmitterEmail() != null)
            notification.addAddressee(new EmailAddressee(td.getSubmitterEmail(), td.getSubmitterEmail()));
        notifications.add(notification);

        return notifications;
    }
}
