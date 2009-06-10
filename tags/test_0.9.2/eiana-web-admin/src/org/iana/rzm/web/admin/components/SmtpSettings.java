package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.admin.model.SmtpConfigVOWrapper;
import org.iana.rzm.web.admin.services.AdminServices;

public abstract class SmtpSettings extends BaseComponent {

     @Component(id="host", type="Insert", bindings = {"value=prop:host"})
     public abstract IComponent getHostComponent();

    @Component(id="port", type="Insert", bindings = {"value=prop:port"})
    public abstract IComponent getPortComponent();

    @Component(id="fromAddress", type="Insert", bindings = {"value=prop:fromAddress"})
    public abstract IComponent getFromAddressComponent();

    @Component(id="mailer", type="Insert", bindings = {"value=prop:mailer"})
    public abstract IComponent getMailerComponent();

    @Component(id="bounceAddress", type="Insert", bindings = {"value=prop:bounceAddress"})
    public abstract IComponent getBounceAddressComponent();

    @Component(id="userName", type="Insert", bindings = {"value=prop:userName"})
    public abstract IComponent getUserNameComponent();

    @Component(id="password", type="Insert", bindings = {"value=prop:password"})
    public abstract IComponent getPasswordComponent();

    @Component(id="ssl", type="Insert", bindings = {"value=prop:ssl"})
    public abstract IComponent getSslComponent();

    @Component(id="tls", type="Insert", bindings = {"value=prop:tls"})
    public abstract IComponent getTlsComponent();

    @Component(id="editible", type="If",  bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibileComponent();

      @Component(id = "edit", type = "DirectLink", bindings = {
        "listener=prop:listener", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @Parameter(required = true)
    public abstract SmtpConfigVOWrapper getSmtpSettings();
    public abstract void setSmtpSettings(SmtpConfigVOWrapper vo);

    @Parameter(required = true)
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEditible();

    @Asset(value = "WEB-INF/admin/SmtpSettings.html")
    public abstract IAsset get$template();

    public void setHost(String host){
        getSmtpSettings().setHost(host);
    }

    public String getHost(){
        return getSmtpSettings().getHost();
    }

    public void setPort(Integer port) {
        getSmtpSettings().setPort(port);
    }

    public Integer getPort(){
        return getSmtpSettings().getPort();
    }

    public void setFromAddress(String fromAddress) {
        getSmtpSettings().setFromAddress(fromAddress);
    }

    public String getFromAddress(){
        return getSmtpSettings().getFromAddress();
    }

    public void setMailer(String mailer) {
        getSmtpSettings().setMailer(mailer);
    }

    public String getMailer(){
        return getSmtpSettings().getMailer();
    }

    public void setUserName(String userName) {
        getSmtpSettings().setUserName(userName);
    }


    public String getUserName(){
        return getSmtpSettings().getUserName();
    }

    public void setPassword(String password) {
        getSmtpSettings().setPassword(password);
    }


    public String getPassword(){
        return getSmtpSettings().getPassword();
    }


    public void setBounceAddress(String bounceAddress) {
        getSmtpSettings().setBounceAddress(bounceAddress);
    }

    public String getBounceAddress(){
        return getSmtpSettings().getBounceAddress();
    }

    public void setSsl(boolean ssl) {
        getSmtpSettings().setSsl(ssl);
    }

    public boolean isSsl(){
        return getSmtpSettings().isSsl();
    }

    public void setTls(boolean tls) {
        getSmtpSettings().setTls(tls);
    }

    public boolean isTls(){
        return  getSmtpSettings().isTls();
    }

    
    
}
