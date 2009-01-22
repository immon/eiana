package org.iana.rzm.startup.jbpm;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;
import org.jbpm.db.MessagingSession;
import org.jbpm.msg.db.DbMessageService;
import org.jbpm.msg.Message;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * @author Patrycja Wegrzynowicz
 */
public class JbpmExceptionHandler implements JbpmCommand {

    public static final String DEFAULT_ERROR_DESTINATION = "ERROR";

    JbpmConfiguration jbpmConfiguration;

    String jbpmContextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

    String errorDestination = DEFAULT_ERROR_DESTINATION;

    public JbpmExceptionHandler(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    public void setJbpmContextName(String jbpmContextName) {
        this.jbpmContextName = jbpmContextName;
    }

    public void setErrorDestination(String errorDestination) {
        this.errorDestination = errorDestination;
    }

    public long execute(Object arg) throws Exception {
        MessageProcessingException e = (MessageProcessingException) arg;
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext(jbpmContextName);
        try {
            // get the message session from the context
            DbMessageService dbMessageSessionImpl = (DbMessageService) jbpmContext.getServices().getMessageService();
            MessagingSession messageSession = dbMessageSessionImpl.getMessagingSession();

            // get the problematic command message from the exception
            Message message = e.message;

            // remove the problematic message from the queue
            dbMessageSessionImpl.receiveByIdNoWait(message.getId());

            message = Message.createCopy(message);

            // update the message with the stack trace
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            message.setException(sw.toString());

            // update the message with the jbpm-error-queue destination
            message.setDestination(errorDestination);

            // resend
            messageSession.save(message);

        } finally {
            jbpmContext.close();
        }
        return 0;
    }
}
