package org.iana.rzm.mail.processor.verisign;

import org.apache.log4j.Logger;
import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.system.trans.TransactionCriteriaFields;
import org.iana.rzm.facade.system.trans.TransactionService;
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
public class VeriSignMailProcessor extends AbstractEmailProcessor {

    private static Logger logger = Logger.getLogger(VeriSignMailProcessor.class);

    private AuthenticationService authenticationService;

    private TransactionService transactionService;

    private MailLogger mailLogger;

    private Config config;

    public VeriSignMailProcessor(TransactionService transactionService,
                                 MailLogger mailLogger,
                                 AuthenticationService authenticationService,
                                 ParameterManager parameterManager) {
        CheckTool.checkNull(transactionService, "transaction service");
        CheckTool.checkNull(mailLogger, "mail logger");
        CheckTool.checkNull(authenticationService, "authentication service");
        CheckTool.checkNull(parameterManager, "parameter manager");
        this.transactionService = transactionService;
        this.mailLogger = mailLogger;
        this.authenticationService = authenticationService;
        this.config = new OwnedConfig(parameterManager);
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof VeriSignMail)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        VeriSignMail mail = (VeriSignMail) msg.getData();
        try {
            authenticate(msg);
            Criterion openForDomain = new And(
                    new Not(new IsNull(TransactionCriteriaFields.END)),
                    new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, mail.getDomainName().toLowerCase())
            );
            List<TransactionVO> list = transactionService.find(openForDomain);
            for (TransactionVO trans : list) {
                mailLogger.logMail(trans.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
            }
        } catch (InfrastructureException e) {
            throw new VerisignProcessException("cannot find transactions for " + mail.getDomainName(), e, msg);
        }
    }

    private void authenticate(Message msg) throws EmailProcessException {
        try {

            String from = msg.getFrom();
            String email = config.getParameter(AuthenticationService.VERISIGN_EMAIL);
            if (from == null || !from.equals(email))
                throw new AuthenticationFailedException(
                        "Message From address: [" + from + "] does not match configured Verisign address [" + email + "]");

            VeriSignMail answer = (VeriSignMail) msg.getData();
            AuthenticationData data = answer.isPgp() ?
                    new PgpMailAuth(msg.getFrom(), msg.getBody()) :
                    new MailAuth(msg.getFrom());
            AuthenticatedUser user = authenticationService.authenticate(data);
            transactionService.setUser(user);

        } catch (ConfigException e) {
            throw new VerisignAuthenticationFailureException("Config exception", e, msg);
        } catch (AuthenticationFailedException e) {
            throw new VerisignAuthenticationFailureException("Authentication failed.", e, msg);
        } catch (AuthenticationRequiredException e) {
            throw new VerisignAuthenticationFailureException("Email authentication not sufficient.", e, msg);
        }
    }
}
