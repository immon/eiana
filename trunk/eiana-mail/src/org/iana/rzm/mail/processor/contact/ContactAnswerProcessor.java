package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
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

    private TransactionService transactionService;

    public ContactAnswerProcessor(TransactionService transactionService) {
        CheckTool.checkNull(transactionService, "transaction service");
        this.transactionService = transactionService;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof ContactAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        ContactAnswer answer = (ContactAnswer) msg.getData();
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

    private void validate(TransactionVO transaction, ContactAnswer answer) throws EmailProcessException {
        if (transaction.getState().getName() != TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION) {
            throw new EmailProcessException("Transaction is not in an appropriate state to perform this operation.");
        }
        // todo: check whether email data conforms transaction data
    }

}
