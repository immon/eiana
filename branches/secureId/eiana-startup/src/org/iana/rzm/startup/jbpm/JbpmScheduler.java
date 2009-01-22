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

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * the jBPM servlet.
 */
public class JbpmScheduler extends HttpServlet {

    public static String DEFAULT_JBPM_CMD_EXECUTOR_NAME = "jbpmCommandExecutor";

    public static String DEFAULT_JBPM_SCHEDULER_NAME = "jbpmScheduler";

    private static final long serialVersionUID = 1L;

    String jbpmCommandExecutorName = null;
    String jbpmSchedulerName = null;

    JbpmThread commandExecutorThread = null;
    JbpmThread schedulerThread = null;

    public void init() throws ServletException {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        this.jbpmCommandExecutorName = getInitParameter("jbpm.context.name", DEFAULT_JBPM_CMD_EXECUTOR_NAME);
        this.jbpmSchedulerName = getInitParameter("jbpm.context.name", DEFAULT_JBPM_SCHEDULER_NAME);

        commandExecutorThread = (JbpmThread) context.getBean(jbpmCommandExecutorName);
        commandExecutorThread.start();

        schedulerThread = (JbpmThread) context.getBean(jbpmSchedulerName);
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
        if (schedulerThread != null) {
            schedulerThread.quit();
        }
        if (commandExecutorThread != null) {
            commandExecutorThread.quit();
        }
    }
}
