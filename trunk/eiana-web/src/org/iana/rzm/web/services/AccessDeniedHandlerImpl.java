package org.iana.rzm.web.services;

import org.apache.tapestry.RedirectException;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.services.ServiceMap;
import org.iana.rzm.facade.auth.AccessDeniedException;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private IEngineService externalService;

    public void handleAccessDenied(AccessDeniedException e, String page) {
        String msg = "You are not authorized to access this resource or preform this operation";
        ILink link = externalService.getLink(true, new ExternalServiceParameter(page, new Object[]{msg}));
        throw new RedirectException(link.getAbsoluteURL());
    }

    public void setExternalService(ServiceMap map) {
        externalService = map.getService(Tapestry.EXTERNAL_SERVICE);
    }
}
