package org.iana.rzm.mail.processor.impactedparty;

import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.processor.IllegalMessageDataException;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.contact.ContactAnswerProcessor;
import org.iana.rzm.mail.processor.contact.ContactAnswer;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartyAnswerProcessor extends ContactAnswerProcessor {

    public ImpactedPartyAnswerProcessor(TransactionService transactionService) {
        super(transactionService);
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof ImpactedPartyAnswer)) throw new IllegalMessageDataException(data);
    }

    protected void validate(TransactionVO transaction, ContactAnswer answer) throws EmailProcessException {
        if (transaction.getState().getName() != TransactionStateVO.Name.PENDING_IMPACTED_PARTIES) {
            throw new EmailProcessException("Transaction is not in an appropriate state to perform this operation.");
        }
    }
}
