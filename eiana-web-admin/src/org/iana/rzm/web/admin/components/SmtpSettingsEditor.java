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
import org.iana.rzm.web.admin.model.SmtpConfigVOWrapper;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.pages.RzmPage;

public abstract class SmtpSettingsEditor extends BaseComponent {
    private static final Integer DEFAULT_SMTP_PORT = 25;

    @Component(id = "smtpEditor", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "success=listener:save",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id="host", type="TextField", bindings = {"value=prop:host", "displayName=literal:Host:", "validators=validators:required"})
    public abstract IComponent getHostComponent();

    @Component(id="port", type="TextField", bindings = {"value=prop:port", "displayName=literal:Port:", "translator=translator:number, pattern=#", "validators=validators:required,min=1"})
    public abstract IComponent getPortComponent();

    @Component(id="fromAddress", type="TextField", bindings = {"value=prop:fromAddress", "displayName=literal:From Address:"})
    public abstract IComponent getFromAddressComponent();

    @Component(id="mailer", type="TextField", bindings = {"value=prop:mailer", "displayName=literal:Mailer:"})
    public abstract IComponent getMailerComponent();

    @Component(id="bounceaddress", type="TextField", bindings = {"value=prop:bounceAddress", "displayName=literal:Bounce Address:"})
    public abstract IComponent getBounceAddressComponent();

    @Component(id="user", type="TextField", bindings = {"value=prop:userName", "displayName=literal:User name:"})
    public abstract IComponent getuserNameComponent();

    @Component(id="password", type="TextField", bindings = {"value=prop:password", "displayName=literal:Password:"})
    public abstract IComponent getPasswordComponent();


    @Component(id = "ssl", type = "Checkbox", bindings = {"value=prop:ssl", "displayName=literal:Use  SSL:"})
    public abstract IComponent getSslCheckBoxComponent();

    @Component(id = "tls", type = "Checkbox", bindings = {"value=prop:tls", "displayName=literal:Use  TLS:"})
    public abstract IComponent getTlsCheckBoxComponent();

    @Component(id = "sslLabel", type = "FieldLabel", bindings = {"field=component:ssl"})
    public abstract IComponent getSslLabelComponent();

    @Component(id = "tlsLabel", type = "FieldLabel", bindings = {"field=component:tls"})
    public abstract IComponent getTlsLabelComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @Asset(value = "WEB-INF/admin/SmtpSettingsEditor.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract SmtpConfigVOWrapper getConfig();

    @Parameter
    public abstract IActionListener getBack();

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
        return port == null || port == 0 ? DEFAULT_SMTP_PORT : port;
    }

    public void setPort(int port){
        getConfig().setPort(port);
    }

    public String getFromAddress(){
        return getConfig().getFromAddress();
    }

    public void setFromAddress(String address){
        getConfig().setFromAddress(address);
    }

    public String getMailer(){
        return getConfig().getMailer();
    }

    public void setMailer(String mailer){
        getConfig().setMailer(mailer);
    }

    public void setBounceAddress(String bounceAddress) {
        getConfig().setBounceAddress(bounceAddress);
    }

    public String getBounceAddress(){
        return getConfig().getBounceAddress();
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

    public boolean isTls(){
        return getConfig().isTls();
    }

    public void setTls(boolean value){
        getConfig().setTls(value);
    }

}
