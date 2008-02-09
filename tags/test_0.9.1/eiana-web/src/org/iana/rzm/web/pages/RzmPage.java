package org.iana.rzm.web.pages;

import org.apache.commons.lang.*;
import org.apache.log4j.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.html.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

public abstract class RzmPage extends BasePage implements MessageProperty {

    @Bean(IanaValidationDelegate.class)
    public abstract IValidationDelegate getValidationDelegate();

    @Bean(org.iana.rzm.web.util.MessageUtil.class)
    public abstract MessageUtil getMessageUtil();
    

    @InitialValue("ognl:null")
    public abstract void setInfoMessage(String value);

    @InitialValue("ognl:null")
    public abstract void setWarningMessage(String value);

    @InitialValue("ognl:null")
    public abstract void setErrorMessage(String value);

    public abstract String getErrorMessage();

    public boolean isHasErrors(){
        return StringUtils.isNotBlank(getErrorMessage());
    }

    protected void log(Logger logger, String msg, Level level) {
        logger.log(level, msg);
    }

    protected void log(Logger logger, String msg, Throwable e) {
        logger.warn(msg, e);
    }

    protected void setErrorField(String componentId, String message) {
        IFormComponent component = (IFormComponent) getComponent(componentId);
        IValidationDelegate delegate = getValidationDelegate();
        delegate.setFormComponent(component);
        delegate.record(message, null);
    }

    public void setErrorField(IFormComponent component, String message) {
        IValidationDelegate delegate = getValidationDelegate();
        delegate.setFormComponent(component);
        delegate.record(message, null);
    }

    protected boolean hasErrors() {
        return getValidationDelegate().getHasErrors() || getErrorMessage() != null ;
    }
    
}

