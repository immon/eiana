package org.iana.rzm.mail.processor.usdoc;

import org.apache.log4j.Logger;
import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
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

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class USDoCAnswerProcessor extends AbstractEmailProcessor {

    private static Logger logger = Logger.getLogger(USDoCAnswerProcessor.class);

    private AuthenticationService authenticationService;

    private AdminTransactionService transactionService;

    private MailLogger mailLogger;

    private Config config;

    private String usdocEmailDomain;

    public USDoCAnswerProcessor(AuthenticationService authenticationService,
                                AdminTransactionService transactionService,
                                MailLogger mailLogger,
                                ParameterManager parameterManager) {
        CheckTool.checkNull(authenticationService, "authentication service");
        CheckTool.checkNull(transactionService, "transaction service");
        CheckTool.checkNull(mailLogger, "mail logger");
        CheckTool.checkNull(parameterManager, "parameter manager");
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
        this.mailLogger = mailLogger;
        this.config = new OwnedConfig(parameterManager);
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof USDoCAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        USDoCAnswer answer = (USDoCAnswer) msg.getData();
        mailLogger.logMail(answer.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
        TransactionVO transaction = null;
        try {
            List<TransactionVO> transactions = transactionService.getByTicketID(answer.getTicketID());
            if (transactions == null || transactions.isEmpty()) {
                throw new USDoCRequestProcessException("Cannot find transaction by ticket-id: " + answer.getTicketID() + ".", msg);
            }
            if (transactions.size() > 1) {
                throw new USDoCRequestProcessException("Ticket-id " + answer.getTicketID() + " is not unique.", msg);
            }
            transaction = transactions.get(0);
            authenticate(msg);
            validate(transaction, answer);
            transactionService.confirmByUSDoC(transaction.getTransactionID(),
                    answer.isNameserverChange(),
                    answer.isAccept());
        } catch (AuthenticationFailedException e) {
            throw new USDoCAuthenticationFailureException(e, msg, transaction);
        } catch (AuthenticationRequiredException e) {
            throw new USDoCAuthenticationFailureException(e, msg, transaction);
        } catch (NoObjectFoundException e) {
            throw new USDoCRequestProcessException("No transaction found with ticket-id: " + answer.getTicketID() + ".", e, msg);
        } catch (InfrastructureException e) {
            throw new USDoCRequestProcessException("Unexpected exception during processing.", e, msg);
        } catch (IllegalTransactionStateException e) {
            throw new USDoCRequestProcessException("Transaction is not in an appropriate state to perform this operation.", e, msg, transaction);
        } catch (AccessDeniedException e) {
            throw new USDoCRequestProcessException("Access denied to perform this operation.", e, msg);
        }
    }

    private void validate(TransactionVO transaction, USDoCAnswer answer) throws EmailProcessException {
        // todo: check whether email data conforms transaction data?
    }

    public void setUsdocEmailDomain(String usdocEmailDomain) {
        this.usdocEmailDomain = usdocEmailDomain;
    }

    private String getUsDoCEmailDomain() {
        try {
            String retValue = config.getParameter(AuthenticationService.USDOC_EMAIL_DOMAIN);
            return (retValue != null)? retValue : this.usdocEmailDomain;

        } catch (ConfigException e) {
            logger.error("config exception", e);
            return this.usdocEmailDomain;
        }
    }

    private void authenticate(Message msg) throws AuthenticationFailedException, AuthenticationRequiredException {

        String usdocEmailDomain = getUsDoCEmailDomain();

        String from = msg.getFrom();

        if (from == null || !from.endsWith(usdocEmailDomain))
            throw new AuthenticationFailedException("Message From domain value: [" + from + "] does not match configured USDoC address domain [" + usdocEmailDomain + "]");

        USDoCAnswer answer = (USDoCAnswer) msg.getData();
        AuthenticationData data = answer.isPgp() ?
                new PgpMailAuth(msg.getFrom(), msg.getBody()) :
                new MailAuth(msg.getFrom());
        
        AuthenticatedUser user = authenticationService.authenticate(data);
        transactionService.setUser(user);
    }
}
