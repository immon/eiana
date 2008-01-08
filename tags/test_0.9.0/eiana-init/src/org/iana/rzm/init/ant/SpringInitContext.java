package org.iana.rzm.init.ant;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.context.ApplicationContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SpringInitContext {

    private static XmlWebApplicationContext context;

    public static ApplicationContext getContext() {
        if (context == null) {
            context = new XmlWebApplicationContext();
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
        }
        return context;
    }
}
