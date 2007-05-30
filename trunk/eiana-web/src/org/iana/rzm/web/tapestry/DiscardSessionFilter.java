package org.iana.rzm.web.tapestry;

import org.apache.tapestry.services.WebRequestServicer;
import org.apache.tapestry.services.WebRequestServicerFilter;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;
import org.apache.tapestry.web.WebSession;
import org.iana.rzm.web.services.ApplicationLifecycle;

import java.io.IOException;

/**
 * Filter, injected into the tapestry.request.WebRequestServicerPipeline configuration, that
 * optionally discards the session at the end of the request (after a logout, typically).
 *
 * @author Howard M. Lewis Ship
 */
public class DiscardSessionFilter implements WebRequestServicerFilter {
    private ApplicationLifecycle _applicationLifecycle;

    public void service(WebRequest request, WebResponse response, WebRequestServicer servicer)
            throws IOException {
        try {
            servicer.service(request, response);
        }
        finally {
            if (_applicationLifecycle.getDiscardSession())
                discardSession(request);
        }
    }

    private void discardSession(WebRequest request) {
        WebSession session = request.getSession(false);

        if (session != null)
            try {
                session.invalidate();
            }
            catch (IllegalStateException ex) {
                // Ignore.
            }
    }

    public void setApplicationLifecycle(ApplicationLifecycle applicationLifecycle) {
        _applicationLifecycle = applicationLifecycle;
    }
} 