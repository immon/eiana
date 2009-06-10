package org.iana.rzm.facade.admin.servlet;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.domain.dns.AdminDNSService;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Piotr Tkaczyk
 */
public class ZoneGeneratorServlet extends HttpServlet {

    private static final String CONTEXT = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.remoting";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        ApplicationContext applicationContext = (ApplicationContext) servletContext.getAttribute(CONTEXT);

        if (applicationContext == null) {
            throw new IllegalStateException("Application context is null.");
        }

        AdminDNSService dnsService = (AdminDNSService) applicationContext.getBean("dnsService");

        try {
            String zoneFileName = dnsService.getExportFileName();
            String zoneFileContent = dnsService.exportZoneFile();

            response.setHeader("Content-length",Integer.toString(zoneFileContent.length()));
            response.setHeader("Content-type","application/octetstream");
            response.setHeader("Content-disposition","inline; filename=" + zoneFileName);

            PrintWriter pw = response.getWriter();
            pw.print(zoneFileContent);
            pw.flush();

        } catch (InfrastructureException e) {
            throw new ServletException(e);
        }

    }

}
