package org.iana.rzm.trans.technicalcheck;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.trans.dns.DNSExceptionMessagesVisitor;
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
public class TechnicalCheckHelper {
    private static final String TEMPLATE_NAME = "failed-technical-check";
    private static final String TEMPLATE_VALUE_DOMAIN_NAME = "domainName";
    private static final String TEMPLATE_VALUE_ERROR_LIST = "errorList";
    private static final String TEMPLATE_VALUE_DAYS = "days";

    public static boolean check(ExecutionContext executionContext, String period) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        NotificationManager notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        NotificationSender notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
        DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");

        String domainName = td.getCurrentDomain().getName();
        Domain retrievedDomain = domainManager.get(td.getCurrentDomain().getName()).clone();

        ObjectChange change = td.getDomainChange();
        if (change != null) {
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
            try {
                DNSTechnicalCheckFactory.getDomainCheck().check(DNSConverter.toDNSDomain(retrievedDomain));
            } catch (DNSTechnicalCheckException e) {
                if (period != null && period.length() > 0) {
                    DNSExceptionMessagesVisitor messagesVisitor = new DNSExceptionMessagesVisitor();
                    e.accept(messagesVisitor);
                    String messages = messagesVisitor.getMessages();

                    Set<Addressee> users = new HashSet<Addressee>();
                    users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, domainName, true, false)).getUsersAbleToAccept());
                    users.addAll(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, domainName, true, false)).getUsersAbleToAccept());
                    if (td.getSubmitterEmail() != null)
                        users.add(new EmailAddressee(td.getSubmitterEmail(), td.getSubmitterEmail()));

                    Notification notification = createNotification(executionContext.getProcessInstance().getId(), domainName, messages, period, users);
                    sendNotification(notification, notificationManagerBean, notificationSender);
                }
                return false;
            }
        }
        return true;
    }

    private static Notification createNotification(Long transactionId, String domainName, String errorMessages, String period, Set<Addressee> to) {
        Map<String, String> values = new HashMap<String, String>();
        values.put(TEMPLATE_VALUE_DOMAIN_NAME, domainName);
        values.put(TEMPLATE_VALUE_ERROR_LIST, errorMessages);
        values.put(TEMPLATE_VALUE_DAYS, period);
        TemplateContent templateContent = new TemplateContent(TEMPLATE_NAME, values);
        Notification notification = new Notification();
        notification.addAllAddressees(to);
        notification.setContent(templateContent);
        return notification;
    }

    private static void sendNotification(Notification notification, NotificationManager notificationManagerBean, NotificationSender notificationSender) throws Exception {
        try {
            if (!notification.getAddressee().isEmpty())
                notificationSender.send(notification.getAddressee(), notification.getContent());
        } catch (NotificationException e) {
            notification.incSentFailures();
            notificationManagerBean.create(notification);
        }
    }
}
