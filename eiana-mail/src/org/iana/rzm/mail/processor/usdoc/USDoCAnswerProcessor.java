package org.iana.rzm.mail.processor.usdoc;

import org.apache.log4j.Logger;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.PNotification;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.IllegalTransactionStateException;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.MailLogger;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.processor.AbstractEmailProcessor;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.processor.IllegalMessageDataException;
import org.iana.rzm.mail.processor.simple.processor.NoRequestEmailProcessException;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCAnswerProcessor extends AbstractEmailProcessor {

    private static Logger logger = Logger.getLogger(USDoCAnswerProcessor.class);

    private AuthenticationService authenticationService;

    private AdminTransactionService transactionService;

    private MailLogger mailLogger;

    private TicketingService ticketingService;

    private static String DEFAULT_REJECTION_COMMENT = "USDoC authentication failed.";

    private NotificationProducer producer;

    private NotificationSender sender;

    private String authenticationMsg = DEFAULT_REJECTION_COMMENT;

    public USDoCAnswerProcessor(AuthenticationService authenticationService,
                                AdminTransactionService transactionService,
                                MailLogger mailLogger,
                                TicketingService service,
                                NotificationProducer producer,
                                NotificationSender sender) {
        CheckTool.checkNull(authenticationService, "authentication service");
        CheckTool.checkNull(transactionService, "transaction service");
        CheckTool.checkNull(mailLogger, "mail logger");
        CheckTool.checkNull(service, "ticketing service");
        CheckTool.checkNull(producer, "notification producer");
        CheckTool.checkNull(sender, "notification sender");
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
        this.mailLogger = mailLogger;
        this.ticketingService = service;
        this.producer = producer;
        this.sender = sender;
    }

    public void setAuthenticationMsg(String authenticationMsg) {
        this.authenticationMsg = authenticationMsg;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof USDoCAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        USDoCAnswer answer = (USDoCAnswer) msg.getData();
        mailLogger.logMail(answer.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
        try {
            List<TransactionVO> transactions = transactionService.getByTicketID(answer.getTicketID());
            if (transactions == null || transactions.isEmpty()) {
                throw new NoRequestEmailProcessException("Cannot find transaction by ticket-id: " + answer.getTicketID() + ".");
            }
            if (transactions.size() > 1) {
                throw new EmailProcessException("Ticket-id " + answer.getTicketID() + " is not unique.");
            }
            TransactionVO transaction = transactions.get(0);
            authenticate(msg);
            validate(transaction, answer);
            transactionService.confirmByUSDoC(transaction.getTransactionID(),
                    answer.isNameserverChange(),
                    answer.isAccept());
        } catch (AuthenticationFailedException e) {
            logger.error("usdoc authentication failed exception", e);
            handleUSDoCAuthenticationFailure(msg, answer);
        } catch (AuthenticationRequiredException e) {
            throw new EmailProcessException("Email authentication not sufficient.", e);
        } catch (NoObjectFoundException e) {
            throw new NoRequestEmailProcessException("No transaction found with ticket-id: " + answer.getTicketID() + ".", e);
        } catch (InfrastructureException e) {
            throw new EmailProcessException("Unexpected exception during processing.", e);
        } catch (IllegalTransactionStateException e) {
            throw new EmailProcessException("Transaction is not in an appropriate state to perform this operation.", e);
        } catch (AccessDeniedException e) {
            throw new EmailProcessException("Access denied to perform this operation.", e);
        }
    }

    private void validate(TransactionVO transaction, USDoCAnswer answer) throws EmailProcessException {
        // todo: check whether email data conforms transaction data?
    }

    private void authenticate(Message msg) throws AuthenticationFailedException, AuthenticationRequiredException {
        USDoCAnswer answer = (USDoCAnswer) msg.getData();
        AuthenticationData data = answer.isPgp() ?
                new PgpMailAuth(msg.getFrom(), msg.getBody()) :
                new MailAuth(msg.getFrom());
        AuthenticatedUser user = authenticationService.authenticate(data);
        transactionService.setUser(user);
    }

    private void handleUSDoCAuthenticationFailure(Message msg, USDoCAnswer answer) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ticket", answer.getTicketID());
            map.put("eppId", answer.getEppID());
            map.put("changeSummary", answer.getChangeSummary());
            map.put("from", msg.getFrom());
            map.put("addressee", new PAddressee(msg.getFrom(), msg.getFrom()));
            map.put("subject", msg.getSubject());
            map.put("content", msg.getBody());
            // there will be a single notification returned by the producer
            for (PNotification notification : producer.produce(map)) {
                sender.send(notification);
            }
            ticketingService.addComment(answer.getTicketID(), authenticationMsg);
        } catch (TicketingException e) {
            logger.error("while handling usdoc authentication failure", e);
        } catch (NotificationProducerException e) {
            logger.error("while handling usdoc authentication failure", e);
        } catch (NotificationSenderException e) {
            logger.error("while handling usdoc authentication failure", e);
        }
    }
}
