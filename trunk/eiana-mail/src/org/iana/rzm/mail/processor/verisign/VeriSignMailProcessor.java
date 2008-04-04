package org.iana.rzm.mail.processor.verisign;

import org.apache.log4j.Logger;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
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

    private TransactionService transactionService;

    private MailLogger mailLogger;

    public VeriSignMailProcessor(TransactionService transactionService, MailLogger mailLogger) {
        CheckTool.checkNull(transactionService, "transaction service");
        CheckTool.checkNull(mailLogger, "mail logger");
        this.transactionService = transactionService;
        this.mailLogger = mailLogger;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof VeriSignMail)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        VeriSignMail mail = (VeriSignMail) msg.getData();
        try {
            Criterion openForDomain = new And(
                new Not(new IsNull(TransactionCriteriaFields.END)),
                new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, mail.getDomainName().toLowerCase())
            );
            List<TransactionVO> list = transactionService.find(openForDomain);
            for (TransactionVO trans : list) {
                mailLogger.logMail(trans.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
            }
        } catch (InfrastructureException e) {
            throw new EmailProcessException("cannot find transactions for " + mail.getDomainName(), e);
        }
    }
}
