package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.notifications.TransactionContentFactory;
import org.iana.rzm.user.AdminRole;
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
    protected TransactionContentFactory transactionTemplateContentFactory;
    protected String notification;
    protected NotificationManager notificationManagerBean;
    protected UserManager userManager;
    protected Long transactionId;
    protected String stateName;
    protected List<String> emails;
    protected boolean sendToContacts = true;
    protected boolean sendToAdmins = true;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        fillDataFromContext(executionContext);
        if (!td.getCurrentDomain().isEnableEmails()) return;
        sendNotifications(getNotifications());
    }

    private void fillDataFromContext(ExecutionContext executionContext) throws NotificationException {
        td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("ticketCommentNotificationSender");
        transactionTemplateContentFactory = (TransactionContentFactory) executionContext.getJbpmContext().getObjectFactory().createObject("transactionTemplateContentFactoryBean");
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

        Domain currentDomain = td.getCurrentDomain();
        Set<Addressee> users = new HashSet<Addressee>();

        // bug: selection: all users in any role for a given domain name!
        if (sendToContacts) {
            Contact adminContact = currentDomain.getAdminContact();
            users.add(new EmailAddressee(adminContact.getEmail(), adminContact.getName()));
            Contact techContact = currentDomain.getTechContact();
            users.add(new EmailAddressee(techContact.getEmail(), techContact.getName()));
            if (td.isRedelegation()) {
                Contact supportingContact = currentDomain.getSupportingOrg();
                users.add(new EmailAddressee(supportingContact.getEmail(), supportingContact.getName()));
            }
        }

        if (sendToAdmins) {
            users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)).getUsersAbleToAccept());
            users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());
            users.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER)).getUsersAbleToAccept());
        }

        if (emails != null && !emails.isEmpty())
            for (String email : emails)
                users.add(new EmailAddressee(email, email));

        String submitterEmail = td.getSubmitterEmail();
        if (CheckTool.isCorrectEmali(submitterEmail))
            users.add(new EmailAddressee(submitterEmail, submitterEmail));

        Content templateContent = transactionTemplateContentFactory.createContent(notification, new HashMap<String, String>(), td);
        Notification notification = new Notification(transactionId, td.getTicketID());
        notification.setContent(templateContent);
        notification.setAddressee(users);
        notifications.add(notification);

        return notifications;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
