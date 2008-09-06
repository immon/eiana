package org.iana.rzm.web.common.services;

import org.apache.tapestry.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.iana.rzm.facade.auth.*;

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
