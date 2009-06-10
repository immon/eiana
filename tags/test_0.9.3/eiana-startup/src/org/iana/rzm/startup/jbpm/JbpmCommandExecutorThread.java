package org.iana.rzm.startup.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;
import org.jbpm.msg.db.StaticNotifier;

import java.io.Serializable;

/**
 * re-used code from CommandExecutorThread (to propertly handle hibernate transaction via Spring AOP)
 */
public class JbpmCommandExecutorThread extends Thread implements JbpmThread, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_COMMAND_EXECUTOR_CONTEXT_NAME = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;


    JbpmConfiguration jbpmConfiguration = null;
    int interval = 5000;
    boolean keepRunning = true;

    JbpmCommand jbpmCommand;

    JbpmCommand jbpmExceptionHandler;

    public JbpmCommandExecutorThread(JbpmConfiguration jbpmConfiguration, JbpmCommand command, JbpmCommand handler) {
        super("JbpmCommandExecutor");
        this.jbpmConfiguration = jbpmConfiguration;
        if (this.jbpmConfiguration == null) {
            throw new JbpmException("jbpmConfiguration is null");
        }
        this.jbpmCommand = command;
        this.jbpmExceptionHandler = handler;
    }

    public void setJbpmConfiguration(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
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
            log.warn("jBPM command executor STOPPED " + keepRunning);
        }
    }

    public void quit() {
        keepRunning = false;
        this.interrupt();
    }

    public boolean executeCommand() throws Exception {
        return jbpmCommand.execute(null) > 0;
    }

    void handleProcessingException(MessageProcessingException e) {
        try {
            jbpmExceptionHandler.execute(e);
        } catch (Exception e1) {
            log.error("handling exception", e1);
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

