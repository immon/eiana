package org.iana.rzm.mail;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.mail.processor.MailsProcessor;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessingAction implements ActionHandler {
    public void execute(ExecutionContext executionContext) throws Exception {
        MailsProcessor mailsProcessor = (MailsProcessor) executionContext.getJbpmContext().getObjectFactory().createObject("mailsProcessor");
        mailsProcessor.process();
    }
}
