package org.iana.rzm.web.tapestry.components.subdomain;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.tapestry.editors.SubDomainAttributeEditor;

public abstract class SubDomainEditor extends BaseComponent {

    @Component(id = "editSubDomain", type = "Form", bindings = {
           "clientValidationEnabled=literal:false",
           "success=listener:save",
           "delegate=prop:validationDelegate"
           })
       public abstract IComponent getEditDomainComponent();

    @Component(id = "registryUrl", type = "TextField", bindings = {"value=prop:registryUrl",
        "displayName=message:registryUrl-label", "validators=validators:registryUrlValidator"})
    public abstract IComponent getRegistryUrlComponent();

    @Component(id = "whois", type = "TextField", bindings = {"value=prop:whoisServer",
        "displayName=message:whoisServer-label", "validators=validators:whoisValidator" })
    public abstract IComponent getWhoisServerComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();


    public abstract String getRegistryUrl();
    public abstract String getWhoisServer();
    public abstract SubDomainAttributeEditor getEditor();

    public IValidationDelegate getValidationDelegate() {
        return getEditor().getValidationDelegate();
    }

    public void save() {
        getEditor().preventResubmission();
        getEditor().save(getRegistryUrl(), getWhoisServer());
    }

    public void revert() {
        getEditor().revert();
    }

}
