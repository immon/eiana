package org.iana.rzm.trans.technicalcheck;

import org.iana.dns.check.DNSExceptionMessagesVisitor;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.trans.dns.DNSTechnicalCheckFactory;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckHelper implements CheckHelper {
    private static final String TEMPLATE_PERIOD_NAME = "failed-technical-check-period";
    private static final String TEMPLATE_NAME = "failed-technical-check";
    private static final String TEMPLATE_VALUE_DOMAIN_NAME = "domainName";
    private static final String TEMPLATE_VALUE_ERROR_LIST = "errorList";
    private static final String TEMPLATE_VALUE_DAYS = "days";

    private ContentFactory templateContentFactory;

    public TechnicalCheckHelper(ContentFactory templateContentFactory) {
        CheckTool.checkNull(templateContentFactory, "template content factory cannot be null");
        this.templateContentFactory = templateContentFactory;
    }

    public boolean check(ExecutionContext executionContext, String period) {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        NotificationManager notificationManager = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        NotificationSender notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
        DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");
        long prosessId = executionContext.getProcessInstance().getId();
        return check(period, td, notificationManager, notificationSender, domainManager, diffConfig, prosessId);
    }

    public boolean check(String period, TransactionData td, NotificationManager notificationManager,
                         NotificationSender notificationSender, DomainManager domainManager,
                         DiffConfiguration diffConfig, Long transactionId) {
        Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();
        ObjectChange change = td.getDomainChange();
        if (change != null) {
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
            return check(retrievedDomain, td.getSubmitterEmail(), period, td, notificationManager, notificationSender, transactionId);
        }
        return true;
    }

    public boolean check(Domain domain, String submitterEmail, String period, TransactionData td,
                         NotificationManager notificationManager, NotificationSender notificationSender,
                         Long transactionId) {
        String domainName = domain.getName();
        try {
            DNSTechnicalCheckFactory.getDomainCheck().check(DNSConverter.toDNSDomain(domain));
        } catch (DNSTechnicalCheckException e) {
            if (domain.isEnableEmails()) {
                DNSExceptionMessagesVisitor messagesVisitor = new DNSExceptionMessagesVisitor();
                e.accept(messagesVisitor);
                String messages = messagesVisitor.getMessages();

                td.setStateMessage(messages);

                Set<Addressee> users = new HashSet<Addressee>();
                users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
                users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());
                if (submitterEmail != null)
                    users.add(new EmailAddressee(submitterEmail, submitterEmail));

                Notification notification = createNotification(transactionId, td.getTicketID(), domainName, messages, period, users);

                sendNotification(notification, notificationManager, notificationSender);
            }
            return false;
        }
        return true;
    }

    public boolean check(Domain domain, String submitterEmail,
                         NotificationManager notificationManager, NotificationSender notificationSender) {
        return check(domain, submitterEmail, null, null, notificationManager, notificationSender, null);
    }

    public boolean check(Domain domain, NotificationManager notificationManager, NotificationSender notificationSender) {
        return check(domain, null, null, null, notificationManager, notificationSender, null);
    }

    private Notification createNotification(Long transactionId, Long ticketId, String domainName, String errorMessages, String period, Set<Addressee> to) {
        Map<String, String> values = new HashMap<String, String>();
        values.put(TEMPLATE_VALUE_DOMAIN_NAME, domainName);
        values.put(TEMPLATE_VALUE_ERROR_LIST, errorMessages);
        values.put(TEMPLATE_VALUE_DAYS, period);
        Content templateContent = templateContentFactory.createContent(
                (period != null && period.length() > 0) ? TEMPLATE_PERIOD_NAME : TEMPLATE_NAME, values);
        Notification notification = new Notification(transactionId, ticketId);
        notification.addAllAddressees(to);
        notification.setContent(templateContent);
        return notification;
    }

    private static void sendNotification(Notification notification, NotificationManager notificationManagerBean, NotificationSender notificationSender) {
        try {
            if (!notification.getAddressee().isEmpty())
                notificationSender.send(notification.getAddressee(), notification.getContent());
        } catch (NotificationException e) {
            notification.incSentFailures();
            notificationManagerBean.create(notification);
        }
    }
}
