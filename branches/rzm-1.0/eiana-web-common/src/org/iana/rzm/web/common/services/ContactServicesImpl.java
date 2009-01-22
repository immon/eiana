package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.tapestry.components.contact.*;

public class ContactServicesImpl implements ContactServices {

    private RzmServices rzmServices;
    private ObjectNotFoundHandler objectNotFoundHandler;
    private AccessDeniedHandler accessDeniedHandler;

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException {
        return rzmServices.getDomain(domainId);
    }

    public void handleObjectNotFound(NoObjectFoundException error, String page) {
        objectNotFoundHandler.handleObjectNotFound(error, page);
    }

    public void handleAccessDenied(AccessDeniedException e, String page) {
        accessDeniedHandler.handleAccessDenied(e, page);
    }


    public void setRzmServices(RzmServices rzmServices) {
        this.rzmServices = rzmServices;
    }

    public void setObjectNotFoundHandler(ObjectNotFoundHandler objectNotFoundHandler) {
        this.objectNotFoundHandler = objectNotFoundHandler;
    }

    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }
}
