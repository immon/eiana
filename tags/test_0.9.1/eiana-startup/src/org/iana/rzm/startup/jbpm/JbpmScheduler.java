/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.iana.rzm.startup.jbpm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.msg.command.CommandExecutorThread;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * the jBPM servlet.
 */
public class JbpmScheduler extends HttpServlet {

    public static String DEFAULT_JBPM_CFG_NAME = "jbpmConfiguration";

    private static Logger log = Logger.getLogger(JbpmScheduler.class);

    private static final long serialVersionUID = 1L;

    String jbpmConfigurationName = null;
    String jbpmContextName = null;

    JbpmConfiguration jbpmConfiguration = null;
    CommandExecutorThread commandExecutorThread = null;
    SchedulerThread schedulerThread = null;

    public void init() throws ServletException {

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        // get the jbpm configuration resource
        this.jbpmConfigurationName = getInitParameter("jbpm.configuration.name");

        if (jbpmConfigurationName == null) {
            this.jbpmConfigurationName = DEFAULT_JBPM_CFG_NAME;
            log.debug("using default jbpm cfg name: " + DEFAULT_JBPM_CFG_NAME);
        } else {
            log.debug("using jbpm cfg resource: '" + jbpmConfigurationName + "'");
        }

        // get the jbpm context to be used from the jbpm configuration
        this.jbpmContextName = getInitParameter("jbpm.context.name");

        if (jbpmContextName == null) {
            log.debug("using default jbpm context");
            jbpmContextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;
        } else {
            log.debug("using jbpm context: '" + jbpmContextName + "'");
        }

        int commandExecutorInterval = Integer.parseInt(getInitParameter("commandExecutorInterval", "5000"));
        int schedulerInterval = Integer.parseInt(getInitParameter("schedulerInterval", "5000"));
        int priority = Integer.parseInt(getInitParameter("priority", "1"));
        int maxResultCount = Integer.parseInt(getInitParameter("maxResultCount", "1"));

        JbpmConfiguration jbpmConfiguration = (JbpmConfiguration) context.getBean(jbpmConfigurationName);

        commandExecutorThread = new CommandExecutorThread(jbpmConfiguration);
        commandExecutorThread.setInterval(commandExecutorInterval);
        commandExecutorThread.setPriority(priority);
        commandExecutorThread.start();

        schedulerThread = new SchedulerThread(jbpmConfiguration);
        schedulerThread.setPriority(priority);
        schedulerThread.setInterval(schedulerInterval);
        schedulerThread.setMaxResultCount(maxResultCount);
        schedulerThread.start();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<h2>JBPM RZM Scheduler</h2><hr />");
        out.println("</body>");
        out.println("</html>");
    }

    protected String getInitParameter(String name, String defaultValue) {
        String value = getInitParameter(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public void destroy() {
        super.destroy();
        if ((schedulerThread != null)
                && (schedulerThread.isAlive())
                ) {
            schedulerThread.quit();
        }
        if ((commandExecutorThread != null)
                && (commandExecutorThread.isAlive())
                ) {
            commandExecutorThread.quit();
        }
    }
}
