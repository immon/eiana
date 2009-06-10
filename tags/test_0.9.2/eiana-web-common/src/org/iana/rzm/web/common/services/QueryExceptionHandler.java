package org.iana.rzm.web.common.services;

import org.iana.web.tapestry.components.browser.*;

public interface QueryExceptionHandler {
    public void handleQeruyException(QueryException error, String page);
}
