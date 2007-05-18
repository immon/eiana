package org.iana.rzm.mail;

import org.iana.mail.MailReceiver;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessingAction implements ActionHandler {
    public void execute(ExecutionContext executionContext) throws Exception {
        MailReceiver mailReceiver = (MailReceiver) executionContext.getJbpmContext().getObjectFactory().createObject("mailReceiver");
        MailsProcessor mailsProcessor = (MailsProcessor) executionContext.getJbpmContext().getObjectFactory().createObject("mailsProcessor");
        List<MimeMessage> messages = mailReceiver.getMessages();
        for (MimeMessage message : messages) {
            String subject = message.getSubject();
            if (!(message.getContent() instanceof String)) {
                throw new Exception("only text messages are supported");
            }
            String content = (String) message.getContent();
            InternetAddress from = new InternetAddress("" + message.getFrom()[0], false);
            mailsProcessor.process(from.getAddress(), subject, content);
        }
    }
}
