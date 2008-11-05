package org.iana.rzm.trans.dns;

import org.iana.dns.check.DNSCheckCollectionResult;
import org.iana.dns.check.DNSExceptionMessagesVisitor;
import org.iana.dns.check.DNSExceptionXMLVisitor;
import org.iana.dns.check.MultipleDNSTechnicalCheckException;
import org.iana.dns.DNSDomain;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PContent;
import org.iana.notifications.PNotification;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.notifications.template.factory.TemplateFactory;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.notifications.TransactionNotificationSender;
import org.iana.rzm.trans.notifications.Notification2Comment;
import org.iana.rzm.trans.process.general.ctx.NotificationContext;
import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckHelper implements CheckHelper {
    private static final String TEMPLATE_PERIOD_NAME = "technical-check-period";
    private static final String TEMPLATE_NAME = "technical-check";
    private static final String TEMPLATE_VALUE_DOMAIN_NAME = "domainName";
    private static final String TEMPLATE_VALUE_PASSED_LIST = "passedList";
    private static final String TEMPLATE_VALUE_ERROR_LIST = "errorList";
    private static final String TEMPLATE_VALUE_DAYS = "days";

    private TemplateFactory templateFactory;

    private DiffConfiguration diffConfig;

    private boolean doTest;

    public TechnicalCheckHelper(TemplateFactory templateFactory, DiffConfiguration diffConfig) {
        this.templateFactory = templateFactory;
        this.diffConfig = diffConfig;
    }

    public boolean check(ExecutionContext executionContext, String period) throws Exception {
        NotificationContext ctx = new NotificationContext(executionContext);
        return check(period,
                ctx.getTransaction(),
                ctx.getSender(),
                ctx.getTicketingService(),
                diffConfig);
    }

    public boolean check(String period,
                         Transaction trans,
                         TransactionNotificationSender sender,
                         TicketingService ticketingService,
                                 DiffConfiguration diffConfig) throws NotificationSenderException, TemplateInstantiationException {
        Domain retrievedDomain = trans.getCurrentDomain().clone();
        ObjectChange change = trans.getDomainChange();
        if (change != null) {
            ChangeApplicator.applyChange(retrievedDomain, change, diffConfig);
            return check(retrievedDomain,
                    period,
                    trans,
                    ticketingService,
                    sender);
        }
        return true;
    }

    public boolean check(Domain domain,
                         String period,
                         Transaction trans,
                         TicketingService ticketingService,
                         TransactionNotificationSender sender) throws TemplateInstantiationException, NotificationSenderException {
        if (doTest) {
            DNSDomain dnsDomain = DNSConverter.toDNSDomain(domain);
            DNSCheckCollectionResult result = DNSTechnicalCheckFactory.getDomainCheck().checkWithResult(dnsDomain);

            PNotification notification = createNotification(domain, trans, result, period);
            sender.send(trans, notification);

            addCommentToRT(ticketingService, notification, trans);

            return result.isSuccess();
        }
        return true;
    }

    private void addCommentToRT(TicketingService ticketingService, PNotification notification, Transaction trans) throws NotificationSenderException {
        Long ticketID = trans.getTicketID();
        if (ticketID != null) {
            try {
                Notification2Comment conv = new Notification2Comment(notification);
                ticketingService.addComment(ticketID, conv.getCommentBody());
            } catch (TicketingException e) {
                throw new NotificationSenderException(e);
            }
        }
    }

    private PNotification createNotification(Domain domain, Transaction trans, DNSCheckCollectionResult result, String period) throws TemplateInstantiationException {
        Map<String, String> values = new HashMap<String, String>();
        values.put(TEMPLATE_VALUE_DOMAIN_NAME, domain.getName());
        values.put(TEMPLATE_VALUE_PASSED_LIST, getPassedMessages(result));
        values.put(TEMPLATE_VALUE_ERROR_LIST, getErrorMessages(domain, trans, result));
        values.put(TEMPLATE_VALUE_DAYS, period);
        String templateName =
                (period != null && period.length() > 0) ?
                        TEMPLATE_PERIOD_NAME : TEMPLATE_NAME;
        Template template = templateFactory.getTemplate(templateName);
        PContent content = template.instantiate(values);
        Set<PAddressee> addressees = getAddressees(domain, trans);
        return new PNotification(addressees, content.getSubject(), content.getBody());
    }

    private Set<PAddressee> getAddressees(Domain domain, Transaction trans) {
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

        return users;
    }

    private String getPassedMessages(DNSCheckCollectionResult result) {
        StringBuffer sb = new StringBuffer();
        List<String> checkNames = result.getSuccessfulCheckNames();
        if (!checkNames.isEmpty()) {
            sb.append("Passed tests: \n");
            for (String checkName : checkNames) {
                sb.append(" - ").append(checkName).append("\n");
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private String getErrorMessages(Domain domain, Transaction trans, DNSCheckCollectionResult result) {
        MultipleDNSTechnicalCheckException e = result.getException();

        if (!e.isEmpty()) {
            DNSExceptionMessagesVisitor messagesVisitor = new DNSExceptionMessagesVisitor();

            e.accept(messagesVisitor);
            String messages = messagesVisitor.getMessages();
            trans.setStateMessage(messages);

            DNSExceptionXMLVisitor xmlVisitor = new DNSExceptionXMLVisitor(domain.toDNSDomain());
            e.accept(xmlVisitor);
            trans.setTechnicalErrors(xmlVisitor.getXML());

            List<String> checkNames = result.getFailedCheckNames();
            StringBuffer sb = new StringBuffer();

            sb.append("Failed tests: \n");
            for (String checkName : checkNames) {
                sb.append(" - ").append(checkName).append("\n");
            }

            sb.append("\n").append(messages);

            return sb.toString();
        } else {
            return "";
        }
    }


    public void setDoTest(boolean doTest) {
        this.doTest = doTest;
    }
}
