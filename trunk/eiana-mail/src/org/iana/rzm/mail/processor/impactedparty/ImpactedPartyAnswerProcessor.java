package org.iana.rzm.mail.processor.impactedparty;

import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.MailLogger;
import org.iana.rzm.mail.processor.contact.ContactAnswerProcessor;
import org.iana.rzm.mail.processor.contact.ContactRequestProcessException;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.processor.IllegalMessageDataException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartyAnswerProcessor extends ContactAnswerProcessor {

    public ImpactedPartyAnswerProcessor(TransactionService transactionService, MailLogger logger) {
        super(transactionService, logger);
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof ImpactedPartyAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void validate(TransactionVO transaction, Message msg) throws EmailProcessException {
        if (transaction.getState().getName() != TransactionStateVO.Name.PENDING_IMPACTED_PARTIES) {
            throw new ContactRequestProcessException("Transaction is not in an appropriate state to perform this operation.", msg, transaction);
        }
    }
}
