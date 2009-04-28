package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.admin.model.PgpConfigVOWrapper;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.pages.RzmPage;


public abstract class IANAPgpEditor extends BaseComponent {

    @Component(id = "editor", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "success=listener:save",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getEditContactComponent();


    @Component(id = "pgp", type = "TextArea", bindings = {"displayName=literal:PGP private Key", "value=prop:key"})
    public abstract IComponent getPublicKeyComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Asset(value = "WEB-INF/admin/IANAPgpEditor.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract PgpConfigVOWrapper getConfig();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    public IValidationDelegate getValidationDelegate(){
        return getRzmPage().getValidationDelegate();
    }

    public String getkey(){
        return getConfig().getKey();
    }

    public void setkey(String key){
        getConfig().setKey(key);
    }

    public void save() {
        getAdminServices().setApplicationConfiguration(getConfig());
        getRzmPage().setInfoMessage("Your changes were save successfully");
    }

    private RzmPage getRzmPage() {
        return ((RzmPage)getPage());
    }

}
