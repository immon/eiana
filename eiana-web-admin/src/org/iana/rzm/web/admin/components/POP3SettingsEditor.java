package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.admin.model.POP3ConfigVOWrapper;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.pages.RzmPage;


public abstract class POP3SettingsEditor extends BaseComponent {
    private static final Integer DEFAULT_POP3_SSL_PORT = 995 ;
    private static final Integer DEFAULT_POP3_PORT = 110;

    @Component(id = "popEditor", type = "Form", bindings = {
        "clientValidationEnabled=literal:false",
        "success=listener:save",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id="host", type="TextField", bindings = {"value=prop:host", "displayName=literal:Host:", "validators=validators:required"})
    public abstract IComponent getHostComponent();

    @Component(id="port", type="TextField", bindings = {"value=prop:port", "displayName=literal:Port:","translator=translator:number", "validators=validators:required,min=1"})
    public abstract IComponent getPortComponent();

    @Component(id="user", type="TextField", bindings = {"value=prop:userName", "displayName=literal:User name:"})
    public abstract IComponent getuserNameComponent();

    @Component(id="password", type="TextField", bindings = {"value=prop:password", "displayName=literal:Password:"})
    public abstract IComponent getPasswordComponent();

    @Component(id = "ssl", type = "Checkbox", bindings = {"value=prop:ssl", "displayName=literal:Use  SSL:"})
    public abstract IComponent getSslCheckBoxComponent();

    @Component(id = "debug", type = "Checkbox", bindings = {"value=prop:debug", "displayName=literal:Debug:"})
    public abstract IComponent getTlsCheckBoxComponent();

    @Component(id = "sslLabel", type = "FieldLabel", bindings = {"field=component:ssl"})
    public abstract IComponent getSslLabelComponent();

    @Component(id = "debugLabel", type = "FieldLabel", bindings = {"field=component:debug"})
    public abstract IComponent getTlsLabelComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();


    @Parameter
    public abstract POP3ConfigVOWrapper getConfig();

    @Parameter
    public abstract IActionListener getBack();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @Asset(value = "WEB-INF/admin/POP3SettingsEditor.html")
    public abstract IAsset get$template();

    public void save(){

        if(getValidationDelegate().getHasErrors()){
            return;
        }
        
        getAdminServices().setApplicationConfiguration(getConfig());
        getBack().actionTriggered(this, getPage().getRequestCycle());
    }

    public void revert(){
        getBack().actionTriggered(this,getPage().getRequestCycle());
    }

    public IValidationDelegate getValidationDelegate(){
        return ((RzmPage)getPage()).getValidationDelegate();
    }

    public String getHost(){
        return getConfig().getHost();
    }

    public void sethost(String host){
        getConfig().setHost(host);
    }

    public int getPort(){
        Integer port = getConfig().getPort();
        if(port == null){
            if(isSsl()){
                return DEFAULT_POP3_SSL_PORT;
            }

            return DEFAULT_POP3_PORT;
        }

        return port;

    }


    public void setPort(int port){
        getConfig().setPort(port);
    }

    public String getUserName(){
        return getConfig().getUserName();
    }

    public void setUserName(String userName){
        getConfig().setUserName(userName);
    }

    public String getPassword(){
        return getConfig().getPassword();
    }

    public void setPassword(String password){
        getConfig().setPassword(password);
    }

    public boolean isSsl(){
        return getConfig().isSsl();
    }

    public void setSsl(boolean value){
        getConfig().setSsl(value);
    }

    public boolean isDebug(){
        return getConfig().isDebug();
    }

    public void setDebug(boolean value){
        getConfig().setDebug(value);
    }

}
