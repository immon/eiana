package org.iana.rzm.trans.dns;

import org.iana.dns.check.DNSExceptionMessagesVisitor;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.notifications.refactored.NotificationSenderException;
import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.PContent;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.template.Template;
import org.iana.notifications.refactored.template.TemplateInstantiationException;
import org.iana.notifications.refactored.template.factory.TemplateFactory;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.notifications.TransactionNotificationSender;
import org.iana.rzm.trans.process.general.ctx.NotificationContext;
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

    private TemplateFactory templateFactory;

    public TechnicalCheckHelper(TemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
    }

    public boolean check(ExecutionContext executionContext, String period) throws Exception {
        DiffConfiguration diffConfig = (DiffConfiguration) executionContext.getJbpmContext().getObjectFactory().createObject("diffConfig");
        NotificationContext ctx = new NotificationContext(executionContext);
        return check(period,
                ctx.getTransaction(),
                ctx.getSender(),
                diffConfig);
    }

    public boolean check(String period,
                         Transaction trans,
                         TransactionNotificationSender sender,
                         DiffConfiguration diffConfig) throws NotificationSenderException, TemplateInstantiationException {
        Domain retrievedDomain = trans.getCurrentDomain().clone();
        ObjectChange change = trans.getDomainChange();
        if (change != null) {
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
            return check(retrievedDomain,
                    period,
                    trans,
                    sender);
        }
        return true;
    }

    public boolean check(Domain domain,
                         String period,
                         Transaction trans,
                         TransactionNotificationSender sender) throws TemplateInstantiationException, NotificationSenderException {
        try {
            DNSTechnicalCheckFactory.getDomainCheck().check(DNSConverter.toDNSDomain(domain));
        } catch (DNSTechnicalCheckException e) {
            DNSExceptionMessagesVisitor messagesVisitor = new DNSExceptionMessagesVisitor();
            e.accept(messagesVisitor);
            String messages = messagesVisitor.getMessages();
            trans.setStateMessage(messages);
            Set<PAddressee> users = new HashSet<PAddressee>();
            if (domain.getAdminContact() != null) {
                Contact contact = domain.getAdminContact();
                users.add(new PAddressee(contact.getName(), contact.getEmail()));
            }
            if (domain.getTechContact() != null) {
                Contact contact = domain.getTechContact();
                users.add(new PAddressee(contact.getName(), contact.getEmail()));
            }
            if (trans.getSubmitterEmail() != null) {
                String submitterEmail = trans.getSubmitterEmail();
                users.add(new PAddressee(submitterEmail, submitterEmail));
            }
            PNotification notification = createNotification(users, domain.getName(), messages, period);
            sender.send(trans, notification);
            return false;
        }
        return true;
    }

    private PNotification createNotification(Set<PAddressee> to, String domainName, String errorMessages, String period) throws TemplateInstantiationException {
        Map<String, String> values = new HashMap<String, String>();
        values.put(TEMPLATE_VALUE_DOMAIN_NAME, domainName);
        values.put(TEMPLATE_VALUE_ERROR_LIST, errorMessages);
        values.put(TEMPLATE_VALUE_DAYS, period);
        String templateName =
                (period != null && period.length() > 0) ?
                        TEMPLATE_PERIOD_NAME : TEMPLATE_NAME;
        Template template = templateFactory.getTemplate(templateName);
        PContent content = template.instantiate(values);
        return new PNotification(to, content.getSubject(), content.getBody());
    }

}
