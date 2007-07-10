package org.iana.rzm.mail;

import org.apache.log4j.Logger;
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
        for (MimeMessage message : messages) mailsProcessor.process(message);
    }
}
