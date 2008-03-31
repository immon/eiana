package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.processor.AbstractEmailProcessor;
import org.iana.rzm.mail.processor.simple.processor.IllegalMessageDataException;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.MailLogger;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMailProcessor extends AbstractEmailProcessor {

    private MailLogger mailLogger;

    public VeriSignMailProcessor(MailLogger mailLogger) {
        CheckTool.checkNull(mailLogger, "mail logger");
        this.mailLogger = mailLogger;
    }

    protected void _acceptable(MessageData data) throws IllegalMessageDataException {
        if (!(data instanceof VeriSignMail)) throw new IllegalMessageDataException(data);
    }

    protected void _process(Message msg) throws EmailProcessException {
        VeriSignMail mail = (VeriSignMail) msg.getData();
        mailLogger.logMail(mail.getTicketID(), msg.getFrom(), msg.getSubject(), msg.getBody());
    }
}



