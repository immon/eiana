package org.iana.rzm.startup.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.command.Command;
import org.jbpm.configuration.ConfigurationException;
import org.jbpm.msg.Message;
import org.jbpm.msg.db.DbMessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * re-used code from executeCommand of CommandExecutorThread (to propertly handle hibernate transaction via Spring AOP)
 */
public class JbpmCommandExecutor implements JbpmCommand {

    JbpmConfiguration jbpmConfiguration;

    String jbpmContextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

    String destination = org.jbpm.command.Command.DEFAULT_CMD_DESTINATION;

    public JbpmCommandExecutor(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    public long execute() throws Exception  {
        long checkForMoreMessages = 0;
        Message message = null;
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext(jbpmContextName);
        try {
          // get a command from the queue
          DbMessageService dbMessageService = null;
          try {
            dbMessageService = (DbMessageService) jbpmContext.getServices().getMessageService();
          } catch (ClassCastException e) {
            throw new ConfigurationException("CommandExecutorThread only works with the DbMessageService implementation of the MessageService. please, configure jbpm.cfg.xml accordingly.");
          }
          if (dbMessageService==null) {
            throw new ConfigurationException("no messaging service available");
          }
          message = dbMessageService.receiveNoWait(destination);

          if (message!=null) {
            checkForMoreMessages = 1;
            Command command = (Command) message;
            log.trace("executing command '"+command+"'");
            command.execute();
          }

        } catch (Exception e) {
          // rollback the transaction
          log.debug("command '"+message+"' threw exception. rolling back transaction", e);
          jbpmContext.setRollbackOnly();

          if (message!=null) {
            throw new MessageProcessingException(message, e);
          } else {
            throw e;
          }
        } finally {
          jbpmContext.close();
        }

        return checkForMoreMessages;
    }

    private static Log log = LogFactory.getLog(JbpmCommandExecutorThread.class);
}
