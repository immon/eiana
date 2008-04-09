package org.iana.rzm.startup.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

/**
 *  re-used code from SchedulerThread (to propertly handle hibernate transaction via Spring AOP)
 */
public class JbpmSchedulerThread extends Thread implements JbpmThread {

    JbpmConfiguration jbpmConfiguration = null;
    String jbpmContextName = null;
    boolean keepRunning = true;
    long interval = 5000;

    JbpmCommand jbpmTimerExecutor;

    public JbpmSchedulerThread(JbpmConfiguration jbpmConfiguration, JbpmCommand command) {
      this(jbpmConfiguration, JbpmContext.DEFAULT_JBPM_CONTEXT_NAME, command);
    }

    public JbpmSchedulerThread(JbpmConfiguration jbpmConfiguration, String jbpmContextName, JbpmCommand command) {
      super("JbpmScheduler");
      this.jbpmConfiguration = jbpmConfiguration;
      this.jbpmContextName = jbpmContextName;
      this.jbpmTimerExecutor = command;
    }

    public void run() {
      while (keepRunning) {
        long millisToWait = interval;
        try {
          millisToWait = executeTimers();

          // calculate the milliseconds to wait...
          if (millisToWait < 0) {
            millisToWait = interval;
          }
          millisToWait = Math.min(millisToWait, interval);

        } catch (RuntimeException e) {
          log.info("runtime exception while executing timers", e);
        } finally {
          try {
            Thread.sleep(millisToWait);
          } catch (InterruptedException e) {
            log.info("waiting for timers got interuppted");
          }
        }
      }
      log.info("ending scheduler thread");
    }

    /**
     * executes due timers and calculates the time before the next timer is due.
     * @return the number of milliseconds till the next job is due or -1 if no timer is
     * schedulerd in the future.
     */
    public long executeTimers() {
        try {
            return jbpmTimerExecutor.execute(null);
        } catch (Exception e) {
            log.error("executeTimers", e);
            return -1;
        }
    }

    // listeners ////////////////////////////////////////////////////////////////

    public void setInterval(long interval) {
      this.interval = interval;
    }

    public void quit() {
      keepRunning = false;
      this.interrupt();
    }

    private static final Log log = LogFactory.getLog(JbpmSchedulerThread.class);
}
