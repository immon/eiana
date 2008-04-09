package org.iana.rzm.startup.jbpm;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;
import org.jbpm.calendar.Duration;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.scheduler.exe.Timer;
import org.jbpm.scheduler.impl.SchedulerListener;
import org.jbpm.db.SchedulerSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *  re-used code from executeTimers of SchedulerThread (to propertly handle hibernate transaction via Spring AOP)
 */
public class JbpmTimerExecutor implements JbpmCommand {

    static BusinessCalendar businessCalendar = new BusinessCalendar();

    JbpmConfiguration jbpmConfiguration;

    String jbpmContextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

    int maxResultCount = 1;

    public JbpmTimerExecutor(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    public void setJbpmContextName(String jbpmContextName) {
        this.jbpmContextName = jbpmContextName;
    }

    public void setMaxResultCount(int maxResultCount) {
        this.maxResultCount = maxResultCount;
    }

    public long execute(Object arg) throws Exception {
        long millisTillNextTimerIsDue = -1;
        boolean isDueDateInPast = true;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext(jbpmContextName);
        try {

          SchedulerSession schedulerSession = jbpmContext.getSchedulerSession();

          log.debug("checking for timers");
          Iterator iter = schedulerSession.findTimersByDueDate(maxResultCount);
          while( (iter.hasNext())
                 && (isDueDateInPast)
               ) {
            Timer timer = (Timer) iter.next();
            log.debug("found timer "+timer);

            // if this timer is due
            if (timer.isDue()) {
              log.debug("executing timer '"+timer+"'");

              // execute
              timer.execute();

              // save the process instance
              jbpmContext.save(timer.getProcessInstance());

              // notify the listeners (e.g. the scheduler servlet)
              notifyListeners(timer);

              // if there was an exception, just save the timer
              if (timer.getException()!=null) {
                schedulerSession.saveTimer(timer);

              // if repeat is specified
              } else if (timer.getRepeat()!=null) {
                // update timer by adding the repeat duration
                Date dueDate = timer.getDueDate();

                // suppose that it took the timer runner thread a
                // very long time to execute the timers.
                // then the repeat action dueDate could already have passed.
                while (dueDate.getTime()<=System.currentTimeMillis()) {
                  dueDate = businessCalendar
                        .add(dueDate,
                          new Duration(timer.getRepeat()));
                }
                timer.setDueDate( dueDate );
                // save the updated timer in the database
                log.debug("saving updated timer for repetition '"+timer+"' in '"+(dueDate.getTime()-System.currentTimeMillis())+"' millis");
                schedulerSession.saveTimer(timer);

              } else {
                // delete this timer
                log.debug("deleting timer '"+timer+"'");
                schedulerSession.deleteTimer(timer);
              }

            } else { // this is the first timer that is not yet due
              isDueDateInPast = false;
              millisTillNextTimerIsDue = timer.getDueDate().getTime() - System.currentTimeMillis();
            }
          }

        } finally {
          jbpmContext.close();
        }

        return millisTillNextTimerIsDue;
    }

    List listeners = new ArrayList();

    public void addListener(SchedulerListener listener) {
      if (listeners==null) listeners = new ArrayList();
      listeners.add(listener);
    }

    public void removeListener(SchedulerListener listener) {
      listeners.remove(listener);
      if (listeners.isEmpty()) {
        listeners = null;
      }
    }

    void notifyListeners(Timer timer) {
      if (listeners!=null) {
        Date now = new Date();
        Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
          SchedulerListener timerRunnerListener = (SchedulerListener) iter.next();
          timerRunnerListener.timerExecuted(now, timer);
        }
      }
    }

    private static Log log = LogFactory.getLog(JbpmSchedulerThread.class);
}
