package org.iana.rzm.web.pages;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;


public abstract class GeneralError extends RzmPage implements IExternalPage {

    public static final String PAGE_NAME = "GeneralError";

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters == null || parameters.length == 0){
            setErrorMessage("ERROR");
        }else{
            setErrorMessage(parameters[0].toString());
        }
    }
}
