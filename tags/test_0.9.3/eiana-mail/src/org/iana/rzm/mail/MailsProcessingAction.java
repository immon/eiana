package org.iana.rzm.mail;

import org.apache.log4j.Logger;
import org.iana.mail.MailReceiver;
import org.iana.rzm.mail.processor.MailsProcessor;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessingAction {

    private static Logger logger = Logger.getLogger(MailsProcessingAction.class);

    private MailReceiver mailReceiver;

    private MailsProcessor mailsProcessor;

    public MailsProcessingAction(MailReceiver mailReceiver, MailsProcessor mailsProcessor) {
        this.mailReceiver = mailReceiver;
        this.mailsProcessor = mailsProcessor;
    }

    public void execute() throws Exception {
        try {
            List<MimeMessage> messages = mailReceiver.getMessages();
            for (MimeMessage message : messages) {
                try {
                    mailsProcessor.process(message);
                } catch (Exception e) {
                    logger.error("while processing the message: " + message, e);
                }
            }
        } catch (Exception e) {
            logger.error("in processing mails actions", e);
        }
    }
}
