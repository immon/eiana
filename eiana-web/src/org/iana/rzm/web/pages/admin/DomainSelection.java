package org.iana.rzm.web.pages.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

public abstract class DomainSelection extends AdminPage implements IExternalPage {

    public static final String PAGE_NAME = "admin/DomainSelection";

    @Component(id = "domainSelection", type = "Form", bindings = {
        "clientValidationEnabled=literal:false",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getFormComponent();

    @Component(id = "next", type = "LinkSubmit", bindings = {"listener=listener:next"})
    public abstract IComponent getNext();

    @Component(id = "cancel", type = "LinkSubmit", bindings = {"listener=listener:cancel"})
    public abstract IComponent getCancel();

    @Component(id = "domain", type = "TextField", bindings = {"displayName=literal:Domain Name:", "value=prop:domainName", "validator=validators:required"})
    public abstract IComponent getDomainComponent();

    @Component(id = "email", type = "TextField", bindings = {"displayName=literal:Submitter Email::", "value=prop:email", "validator=validators:email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "comment", type = "TextArea", bindings = {
        "displayName=literal:Comment:", "value=prop:comment"})
    public abstract IComponent getTextComponent();

    @InjectPage(DomainView.PAGE_NAME)
    public abstract DomainView getDomainView();

    @Persist("client:form")
    public abstract ICallback getCallback();

    public abstract void setCallback(ICallback callback);

    public abstract String getDomainName();
    public abstract void setDomainName(String name);
    public abstract void setComment(String comment);
    public abstract String getComment();
    public abstract String getEmail();
    public abstract void setEmail(String email);

    protected Object[] getExternalParameters() {
        return new Object[]{getCallback(), getDomainName(), getEmail(), getComment()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length < 1 ){
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
        }

        setCallback((ICallback) parameters[0]);
        setDomainName(parameters[1].toString());

        Object email = parameters[2];
        if(email != null){
            setEmail(email.toString());
        }

        Object comment = parameters[3];
        if(comment != null){
            setComment(comment.toString());
        }
    }

    public void next() {
        String name = getDomainName();
        IValidationDelegate validationDelegate = getValidationDelegate();
        DomainVOWrapper domain = null;
        if (StringUtils.isEmpty(name)) {
            validationDelegate.record("Please specify domain name", ValidationConstraint.REQUIRED);
            return;
        }
        try {
           domain  = getAdminServices().getDomain(getDomainName());
        } catch (NoObjectFoundException e) {
            validationDelegate.record(e.getMessage(), ValidationConstraint.REQUIRED);
            return;
        }

        getVisitState().markAsVisited(domain);
        getVisitState().setSubmitterEmail(getEmail());
        DomainView page = getDomainView();
        page.setOriginalDomain(domain);
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters()));
        page.setDomainId(domain.getId());
        getRequestCycle().activate(page);
    }

    public void cancel() {
        getCallback().performCallback(getRequestCycle());
    }

    public void resetStateIfneeded() {
        getVisitState().resetModifirdDomain();
    }

}
