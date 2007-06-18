package org.iana.rzm.web.services;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.apache.tapestry.services.ServiceMap;
import org.iana.rzm.facade.common.NoObjectFoundException;

public class ObjectNotFoundHandlerImpl implements ObjectNotFoundHandler {

    private IRequestCycle requestCycle;
    private ApplicationStateManager stateManager;
    private IEngineService externalService;


    public void handleObjectNotFound(NoObjectFoundException error, String page) {
        long id = error.getId();
        String name = error.getName();
        String message = buildMessage(id, name);
        ILink link = externalService.getLink(true, new ExternalServiceParameter(page, new Object[]{message}));
        throw new RedirectException(link.getAbsoluteURL());
    }

    private String buildMessage(long id, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("SYSTEM ERROR").append("\n");
        if (id != 0 && name != null) {
            builder.append("Could not find Entity with ID = ").append(id).append(" and NAME ").append(name);
        } else if (id != 0) {
            builder.append("Could not find Entity with ID = ").append(id);
        } else if (name != null) {
            builder.append("Could not find Entity with NAME = ").append(name);
        }

        return builder.toString();
    }

    public void setStateManager(ApplicationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setRequestCycle(IRequestCycle requestCycle) {
        this.requestCycle = requestCycle;
    }


    public void setExternalService(ServiceMap map) {
        externalService = map.getService(Tapestry.EXTERNAL_SERVICE);
    }
}
