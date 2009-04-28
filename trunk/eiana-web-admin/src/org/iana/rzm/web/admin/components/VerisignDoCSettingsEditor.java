package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.admin.model.VerisignDoCConfig;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.pages.RzmPage;


public abstract class VerisignDoCSettingsEditor extends BaseComponent {

    @Component(id = "editor", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "success=listener:save",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getEditContactComponent();

    @Component(id="title", type="Insert", bindings = {"value=prop:title"})
    public abstract IComponent getTitleComponent();

    @Component(id = "email", type = "TextField", bindings = {"value=prop:email", "displayName=literal:Email Address",
            "validators=validators:required, email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "pgp", type = "TextArea", bindings = {"displayName=literal:PGP Key", "value=prop:key"})
    public abstract IComponent getPublicKeyComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();


    @Asset(value = "WEB-INF/admin/VerisignDoCSettingsEditor.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract VerisignDoCConfig getConfig();

    @Parameter
    public abstract String getTitle();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    public IValidationDelegate getValidationDelegate(){
        return ((RzmPage)getPage()).getValidationDelegate();
    }

    public String getEmail() {
        return getConfig().getEmail();
    }

    public void setEmail(String email) {
        getConfig().setEmail(email);
    }

    public String getKey() {
        return getConfig().getKey();
    }

    public void setKey(String key) {
        getConfig().setKey(key);
    }


    public void save() {
        getAdminServices().setApplicationConfiguration(getConfig());
        getRzmPage().setInfoMessage("Your changes were save successfully");
    }

    private RzmPage getRzmPage() {
        return (RzmPage) getPage();
    }

}
