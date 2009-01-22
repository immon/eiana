package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.mail.processor.*;
import org.iana.rzm.mail.processor.simple.data.*;
import org.iana.rzm.mail.processor.simple.processor.*;

import java.util.*;

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
        try {
            List<TransactionVO> transactions = transactionService.getByTicketID(answer.getTicketID());
            if (transactions == null || transactions.isEmpty()) {
                throw new EmailProcessException("Cannot find transaction by ticket-id: " + answer.getTicketID() + ".");
            }
            if (transactions.size() > 1) {
                throw new EmailProcessException("Ticket-id " + answer.getTicketID() + " is not unique.");
            }
            TransactionVO transaction = transactions.get(0);
            validate(transaction, answer);
            if (answer.isAccept()) {
                transactionService.acceptTransaction(transaction.getTransactionID(), answer.getToken());
            } else {
                transactionService.rejectTransaction(transaction.getTransactionID(), answer.getToken());
            }
        } catch (NoObjectFoundException e) {
            throw new EmailProcessException("No transaction found with ticket-id: " + answer.getTicketID() + ".", e);
        } catch (InfrastructureException e) {
            throw new EmailProcessException("Unexpected exception during processing.", e);
        }
    }

    protected void validate(TransactionVO transaction, ContactAnswer answer) throws EmailProcessException {
        if (transaction.getState().getName() != TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION) {
            throw new EmailProcessException("Transaction is not in an appropriate state to perform this operation.");
        }
        // todo: check whether email data conforms transaction data
    }

}
