package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.IllegalTransactionStateException;
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
public class USDoCAnswerProcessor extends AbstractEmailProcessor {

    private AuthenticationService authenticationService;

    private AdminTransactionService transactionService;


    public USDoCAnswerProcessor(AuthenticationService authenticationService, AdminTransactionService transactionService) {
        CheckTool.checkNull(authenticationService, "authentication service");
        CheckTool.checkNull(transactionService, "transaction service");
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof USDoCAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        USDoCAnswer answer = (USDoCAnswer) msg.getData();
        try {
            List<TransactionVO> transactions = transactionService.getByTicketID(answer.getTicketID());
            if (transactions == null || transactions.isEmpty()) {
                throw new EmailProcessException("Cannot find transaction by ticket-id: " + answer.getTicketID() + ".");
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
        } catch (NoObjectFoundException e) {
            throw new EmailProcessException("No transaction found with ticket-id: " + answer.getTicketID() + ".", e);
        } catch (InfrastructureException e) {
            throw new EmailProcessException("Unexpected exception during processing.", e);
        } catch (IllegalTransactionStateException e) {
            throw new EmailProcessException("Transaction is not in an appropriate state to perform this operation.", e);
        } catch (AccessDeniedException e) {
            throw new EmailProcessException("Access denied to perform this operation.", e);
        }
    }

    private void validate(TransactionVO transaction, USDoCAnswer answer) throws EmailProcessException {
        // todo: check whether email data conforms transaction data
    }

    private void authenticate(Message msg) throws EmailProcessException {
        try {
            USDoCAnswer answer = (USDoCAnswer) msg.getData();
            AuthenticationData data = answer.isPgp() ?
                    new PgpMailAuth(msg.getFrom(), msg.getBody()) :
                    new MailAuth(msg.getFrom());
            AuthenticatedUser user = authenticationService.authenticate(data);
            transactionService.setUser(user);
        } catch (AuthenticationFailedException e) {
            throw new EmailProcessException("Authentication failed.", e);
        } catch (AuthenticationRequiredException e) {
            throw new EmailProcessException("Email authentication not sufficient.", e);
        }
    }
}
