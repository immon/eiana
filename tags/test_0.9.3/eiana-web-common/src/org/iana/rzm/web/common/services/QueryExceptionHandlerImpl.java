package org.iana.rzm.web.common.services;

import org.apache.tapestry.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.iana.web.tapestry.components.browser.*;

public class QueryExceptionHandlerImpl implements QueryExceptionHandler {

    private IEngineService externalService;

    public void  handleQeruyException(QueryException error, String page) {
        ILink link = externalService.getLink(true, new ExternalServiceParameter(page, new Object[]{error.getErrorMessage()}));
        throw new RedirectException(link.getAbsoluteURL());
    }

    public void setExternalService(ServiceMap map) {
        externalService = map.getService(Tapestry.EXTERNAL_SERVICE);
    }


}
