package org.iana.rzm.startup.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;
import org.jbpm.db.MessagingSession;
import org.jbpm.msg.Message;
import org.jbpm.msg.db.DbMessageService;
import org.jbpm.msg.db.StaticNotifier;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * re-used code from CommandExecutorThread (to propertly handle hibernate transaction via Spring AOP)
 */
public class JbpmCommandExecutorThread extends Thread implements JbpmThread, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_COMMAND_EXECUTOR_CONTEXT_NAME = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;
    public static final String DEFAULT_ERROR_DESTINATION = "ERROR";


    JbpmConfiguration jbpmConfiguration = null;
    String jbpmContextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;
    String errorDestination = DEFAULT_ERROR_DESTINATION;
    int interval = 5000;
    boolean keepRunning = true;

    JbpmCommand jbpmCommand;

    public JbpmCommandExecutorThread(JbpmConfiguration jbpmConfiguration, JbpmCommand command) {
        super("JbpmCommandExecutor");
        this.jbpmConfiguration = jbpmConfiguration;
        if (this.jbpmConfiguration == null) {
            throw new JbpmException("jbpmConfiguration is null");
        }
        this.jbpmCommand = command;
    }

    public void setErrorDestination(String errorDestination) {
        this.errorDestination = errorDestination;
    }

    public void setJbpmConfiguration(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    public void setJbpmContextName(String jbpmContextName) {
        this.jbpmContextName = jbpmContextName;
    }

    public void run() {
        // while not interrupted...
        try {

            while (keepRunning) {
                try {
                    boolean checkForMoreMessages = true;
                    try {
                        checkForMoreMessages = executeCommand();

                    } catch (MessageProcessingException e) {
                        log.error(e);
                        handleProcessingException(e);
                    } catch (InterruptedException e) {
                        throw e;
                    } catch (Exception other) {
                        // NOTE that Error's are not caught because that might halt the JVM and mask the original Error.
                        log.error(other);
                        Thread.sleep(5000);
                    }

                    if (!checkForMoreMessages) {
                        log.debug("waiting for more messages");
                        StaticNotifier.waitForNotification(Command.DEFAULT_CMD_DESTINATION);
                    }
                } catch (InterruptedException e) {
                    log.info("jBPM command executor got interrupted");
                }
            }
        } finally {
            log.warn("jBPM command executor STOPPED");
        }
    }

    public void quit() {
        keepRunning = false;
        this.interrupt();
    }

    public boolean executeCommand() throws Exception {
        return jbpmCommand.execute() > 0;
    }

    void handleProcessingException(MessageProcessingException e) {
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
    }

    public synchronized void start() {
        super.start();
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    private static Log log = LogFactory.getLog(JbpmCommandExecutorThread.class);
}

