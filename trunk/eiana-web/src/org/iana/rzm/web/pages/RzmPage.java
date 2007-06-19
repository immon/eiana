package org.iana.rzm.web.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.InjectStateFlag;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.tapestry.IanaValidationDelegate;
import org.iana.rzm.web.util.MessageUtil;

public abstract class RzmPage extends BasePage implements MessageProperty {

    @Bean(IanaValidationDelegate.class)
    public abstract IValidationDelegate getValidationDelegate();

    @Bean(org.iana.rzm.web.util.MessageUtil.class)
    public abstract MessageUtil getMessageUtil();

    @InjectState("visit")
    public abstract Visit getVisitState();

    @InjectStateFlag("visit")
    public abstract boolean getVisitStateExists();

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



}

