package org.iana.rzm.web.common.services;

import org.apache.tapestry.*;
import org.iana.web.tapestry.feedback.*;

public class ExternalPageErrorHandlerImpl implements ExternalPageErrorHandler {

    private IRequestCycle requestCycle;
    private String pageName;

    public void handleExternalPageError(String error) {
        MessageProperty page = (MessageProperty) requestCycle.getPage(pageName);
        page.setErrorMessage(error);
        requestCycle.activate(page);
    }

     public void setRequestCycle(IRequestCycle requestCycle) {
        this.requestCycle = requestCycle;
    }

    public void setPageName(String name){
        this.pageName = name;
    }
    
}
