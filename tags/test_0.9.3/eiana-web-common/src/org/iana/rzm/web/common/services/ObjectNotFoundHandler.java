package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.common.*;

public interface ObjectNotFoundHandler {
    public void handleObjectNotFound(NoObjectFoundException error, String page);
}
