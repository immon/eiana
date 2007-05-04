package org.iana.rzm.init.ant;

import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.dao.JbpmProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.jbpm.JbpmContextFactory;
import org.iana.rzm.trans.jbpm.JbpmContextFactoryBean;
import org.jbpm.JbpmConfiguration;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.core.io.FileSystemResourceLoader;

/**
 * It deploys all the needed jBPM process definitions (at this moment it's only the unified workflow process definition).
 *
 * @author Patrycja Wegrzynowicz
 */
public class ProcessDeployment {
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
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
    }
}
