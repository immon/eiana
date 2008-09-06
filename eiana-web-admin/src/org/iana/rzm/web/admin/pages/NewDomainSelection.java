package org.iana.rzm.web.admin.pages;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.common.model.*;

public abstract class NewDomainSelection extends AdminPage {
    public static final String PAGE_NAME = "admin/NewDomainSelection";

    @Component(id = "domainSelection", type = "Form", bindings = {
        "clientValidationEnabled=literal:false",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getFormComponent();

    @Component(id = "next", type = "LinkSubmit", bindings = {"listener=listener:next"})
    public abstract IComponent getNext();

    @Component(id = "cancel", type = "LinkSubmit", bindings = {"listener=listener:cancel"})
    public abstract IComponent getCancel();

    @Component(id = "domain",
               type = "TextField",
               bindings = {"displayName=literal:Domain Name:", "value=prop:domainName", "validator=validators:required"})
    public abstract IComponent getDomainComponent();

    @Component(id = "country", type = "TextField", bindings = {"displayName=literal:Country:", "value=prop:country", "validator=validators:required"})
    public abstract IComponent getCountryComponent();

    @Component(id = "countryCode", type = "TextField", bindings = {"displayName=literal:Country Code:", "value=prop:countryCode", "validator=validators:required"})
    public abstract IComponent getCountryCodeComponent();

    @InjectPage(CreateDomain.PAGE_NAME)
    public abstract CreateDomain getCreateDomainPage();

    @InjectPage(Domains.PAGE_NAME)
    public abstract Domains getDomainsPage();

    @Persist("client")
    public abstract void setCallback(ICallback callback);
    

    public abstract String getDomainName();
    public abstract void setDomainName(String name);

    public abstract String getCountryCode();
    public abstract void setCountryCode(String code);

    public abstract String getCountry();
    public abstract void setCountry(String country);

    protected Object[] getExternalParameters() {
        return new Object[]{getDomainName(), getCountry(), getCountryCode()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 3) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        setDomainName(parameters[0].toString());
        setCountry(parameters[1].toString());
        setCountryCode(parameters[2].toString());

    }

    public void next() {
        String name = getDomainName();
        IValidationDelegate validationDelegate = getValidationDelegate();
        if (StringUtils.isEmpty(name)) {
            validationDelegate.record("Please specify domain name", ValidationConstraint.REQUIRED);
            return;
        }

        String name1 = getAdminServices().getCountryName(name);
        if(!name1.equals("Top Level Domain")){
            validationDelegate.record("Domain name already in use", ValidationConstraint.CONSISTENCY);
            return;
        }

        SystemDomainVOWrapper domain = new SystemDomainVOWrapper();
        domain.setName(getDomainName());
        CreateDomain page = getCreateDomainPage();
        page.setCountryName(getCountry());
        page.setCountryCode(getCountryCode());
        page.setDomain(domain);
        page.setOriginalDomain(new SystemDomainVOWrapper());
        getVisitState().storeDomain(domain);
        getRequestCycle().activate(page);
    }

    public void cancel() {
        getRequestCycle().activate(getDomainsPage());
    }
}
    