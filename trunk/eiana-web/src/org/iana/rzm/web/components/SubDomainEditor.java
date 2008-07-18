package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.common.*;

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
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Asset(value = "WEB-INF/SubDomainEditor.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract String getRegistryUrl();

    @Parameter
    public abstract String getWhoisServer();

    @Parameter
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
