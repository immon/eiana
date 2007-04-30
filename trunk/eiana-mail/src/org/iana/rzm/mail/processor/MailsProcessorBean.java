package org.iana.rzm.mail.processor;

import org.iana.mail.MailReceiver;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.MailAuth;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.mail.parser.MailData;
import org.iana.rzm.mail.parser.MailParser;
import org.iana.rzm.mail.parser.ConfirmationMailData;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessorBean implements MailsProcessor {
    private MailReceiver receiver;
    private MailParser parser;
    private AuthenticationService authSvc;
    private SystemTransactionService transSvc;


    public MailsProcessorBean(MailReceiver receiver, MailParser parser, AuthenticationService authSvc, SystemTransactionService transSvc) {
        this.receiver = receiver;
        this.parser = parser;
        this.authSvc = authSvc;
        this.transSvc = transSvc;
    }

    public void process() throws MailsProcessorException {
        try {
            List<MimeMessage> messages = receiver.getMessages();
            for (MimeMessage message : messages) {
                String subject = message.getSubject();
                if (!(message.getContent() instanceof String)) {
                    throw new Exception("only text messages are supported");
                }
                String content = (String) message.getContent();
                InternetAddress from = new InternetAddress("" + message.getFrom()[0], false);
                MailData mailData = parser.parse(from.getAddress(), subject, content);
                if (mailData instanceof ConfirmationMailData)
                    processConfirmation((ConfirmationMailData) mailData, authSvc, transSvc);
                else
                    throw new Exception("unsupported mail type");
            }
        } catch (Exception e) {
            throw new MailsProcessorException("while processing emails", e);
        }
    }

    private void processConfirmation(ConfirmationMailData cmd, AuthenticationService authSvc,
                                     SystemTransactionService transSvc) throws Exception {
        AuthenticatedUser user = authSvc.authenticate(new MailAuth(cmd.getEmail()));
        transSvc.setUser(user);
        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addTickedId(cmd.getTicketId());
        List<TransactionVO> transactions = transSvc.findTransactions(criteria);
        if (transactions.size() !=1)
            throw new Exception("unexpected number of transaction with ticket id = " + cmd.getTicketId());
        TransactionVO trans = transactions.iterator().next();
        if (!cmd.getStateName().equals(trans.getState().getName().toString()))
            throw new Exception("wrong transaction state = " + cmd.getStateName() +
                    " expected: " + trans.getState().getName());
        if (cmd.isAccepted())
            transSvc.acceptTransaction(trans.getTransactionID());
        else
            transSvc.rejectTransaction(trans.getTransactionID());
    }
}
