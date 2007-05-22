package org.iana.rzm.init.ant;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.core.io.FileSystemResourceLoader;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ProcessCreation {

    public static void main(String[] args) {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        String[] config = new String[]{"file:../conf/spring/services-config.xml"};
        MockServletContext mockServletContext = new MockServletContext(new FileSystemResourceLoader());

        context.setConfigLocations(config);
        context.setServletContext(mockServletContext);
        context.setServletConfig(new MockServletConfig());
        context.refresh();

        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        request.setSession(session);

        ProcessDAO processDAO = (ProcessDAO) context.getBean("processDAO");
        try {
            ProcessDefinition pd = DefinedTestProcess.getDefinition(args[0]);
            processDAO.deploy(pd);
            ProcessInstance pi = processDAO.newProcessInstance(pd.getName());
            pi.signal();
        } finally {
            processDAO.close();
        }
    }
}
