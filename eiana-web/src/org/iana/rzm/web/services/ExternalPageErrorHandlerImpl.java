package org.iana.rzm.web.services;

import org.apache.tapestry.IRequestCycle;
import org.iana.rzm.web.pages.MessageProperty;

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
