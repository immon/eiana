package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;


public abstract class BaseGeneralError extends RzmPage implements IExternalPage {
    public static final String PAGE_NAME = "GeneralError";

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters == null || parameters.length == 0){
            setErrorMessage("ERROR");
        }else{
            setErrorMessage(parameters[0].toString());
        }
    }
}
