package org.iana.rzm.web.admin.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.RequestMetaParameters;
import org.iana.rzm.web.common.callback.RzmCallback;
import org.iana.rzm.web.common.model.DomainVOWrapper;

public abstract class DomainSelection extends AdminPage implements IExternalPage {

    public static final String PAGE_NAME = "DomainSelection";

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

    @Persist("client")
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
            return;
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
        getVisitState().setRequestMetaParameters(new RequestMetaParameters(getEmail(), getComment()));
        DomainView page = getDomainView();
        page.setRequestMetaParameters(getVisitState().getRequestMetaParameters());
        page.setOriginalDomain(domain);
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        page.setDomainId(domain.getId());
        getRequestCycle().activate(page);
    }

    public void cancel() {
        getCallback().performCallback(getRequestCycle());
    }

    public void resetStateIfneeded() {
        getVisitState().resetAllModifiedDomain();
    }

}
