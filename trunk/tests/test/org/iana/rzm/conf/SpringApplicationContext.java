package org.iana.rzm.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.core.io.FileSystemResourceLoader;

/**
 * @author Piotr Tkaczyk
 */
public class SpringApplicationContext {
    public static final String CONFIG_FILE_NAME = "file:../conf/spring/services-config.xml";

    private static SpringApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringApplicationContext() {
    }

    public static SpringApplicationContext getInstance() {
        if (instance == null)
            instance = new SpringApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if (appCtx == null) {
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            String[] config = new String[]{CONFIG_FILE_NAME};
            MockServletContext mockServletContext = new MockServletContext(new FileSystemResourceLoader());

            context.setConfigLocations(config);
            context.setServletContext(mockServletContext);
            context.setServletConfig(new MockServletConfig());
            context.refresh();

            MockHttpSession session = new MockHttpSession();
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            request.setSession(session);
/*
            mockServletContext.setAttribute(WebApplicationCont ext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,ctx);
            mockSession = new MockHttpSession(mockServletContext);
            request.setSession(mockSession);
*/
            appCtx = context;
        }
        return appCtx;
    }
}
