package org.iana.rzm.web.services;

import org.iana.rzm.facade.common.NoObjectFoundException;

public interface ObjectNotFoundHandler {
    public void handleObjectNotFound(NoObjectFoundException error, String page);
}
