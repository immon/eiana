package org.iana.rzm.init.ant;

import org.springframework.context.*;
import org.springframework.core.io.*;
import org.springframework.mock.web.*;
import org.springframework.web.context.request.*;
import org.springframework.web.context.support.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SpringInitContext {

    private static XmlWebApplicationContext context;

    public static ApplicationContext getContext() {
        if (context == null) {
            context = new XmlWebApplicationContext();
            String[] config = new String[]{"file:../tests/spring/services-test-config.xml"};
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
