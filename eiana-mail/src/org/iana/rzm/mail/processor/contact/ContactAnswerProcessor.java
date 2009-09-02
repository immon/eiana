package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.MailLogger;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.processor.AbstractEmailProcessor;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.processor.IllegalMessageDataException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactAnswerProcessor extends AbstractEmailProcessor {

    protected TransactionService transactionService;

    private MailLogger mailLogger;

    public ContactAnswerProcessor(TransactionService transactionService, MailLogger logger) {
        CheckTool.checkNull(transactionService, "transaction service");
        CheckTool.checkNull(logger, "mail logger");
        this.transactionService = transactionService;
        this.mailLogger = logger;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof ContactAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        ContactAnswer answer = (ContactAnswer) msg.getData();
        mailLogger.logMail(answer.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
        TransactionVO transaction = null;
        try {
            List<TransactionVO> transactions = transactionService.getByTicketID(answer.getTicketID());
            if (transactions == null || transactions.isEmpty()) {
                throw new ContactRequestProcessException("Cannot find transaction by ticket-id: " + answer.getTicketID() + ".", msg);
            }
            if (transactions.size() > 1) {
                throw new ContactRequestProcessException("Ticket-id " + answer.getTicketID() + " is not unique.", msg);
            }
            transaction = transactions.get(0);
            validate(transaction, msg);
            if (answer.isAccept()) {
                transactionService.acceptTransaction(transaction.getTransactionID(), answer.getToken());
            } else {
                transactionService.rejectTransaction(transaction.getTransactionID(), answer.getToken());
            }
        } catch (NoObjectFoundException e) {
            throw new ContactRequestProcessException("No transaction found with ticket-id: " + answer.getTicketID() + ".", e, msg, transaction);
        } catch (InfrastructureException e) {
            throw new ContactRequestProcessException("Unexpected exception during processing.", e, msg, transaction);
        }
    }

    protected void validate(TransactionVO transaction, Message msg) throws EmailProcessException {
        if (transaction.getState().getName() != TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION) {
            throw new ContactRequestProcessException("Transaction is not in an appropriate state to perform this operation.", msg, transaction);
        }
        // todo: check whether email data conforms transaction data
    }

}
