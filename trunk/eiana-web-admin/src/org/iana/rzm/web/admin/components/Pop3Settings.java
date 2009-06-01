package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.admin.model.POP3ConfigVOWrapper;
import org.iana.rzm.web.admin.services.AdminServices;

public abstract class Pop3Settings extends BaseComponent {

     @Component(id="host", type="Insert", bindings= {"value=prop:host"})
     public abstract IComponent getHostComponent();

    @Component(id="port", type="Insert", bindings = {"value=prop:port"})
    public abstract IComponent getPortComponent();


    @Component(id="userName", type="Insert", bindings = {"value=prop:userName"})
    public abstract IComponent getUserNameComponent();

    @Component(id="password", type="Insert", bindings = {"value=prop:password"})
    public abstract IComponent getPasswordComponent();

    @Component(id="ssl", type="Insert", bindings = {"value=prop:ssl"})
    public abstract IComponent getSslComponent();

    @Component(id="debug", type="Insert", bindings = {"value=prop:debug"})
    public abstract IComponent getTlsComponent();

    @Component(id="editible", type="If",  bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibileComponent();

    @Component(id = "edit", type = "DirectLink", bindings = {
        "listener=prop:listener", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @Parameter(required = true)
    public abstract POP3ConfigVOWrapper getPop3Settings();
    public abstract void setPop3Settings(POP3ConfigVOWrapper vo);

    @Parameter(required = true)
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEditible();

    @Asset(value = "WEB-INF/admin/Pop3Settings.html")
    public abstract IAsset get$template();


    public void setHost(String host){
        getPop3Settings().setHost(host);
    }

    public String getHost(){
        return getPop3Settings().getHost();
    }

    public void setPort(Integer port) {
        getPop3Settings().setPort(port);
    }

    public  Integer getPort(){
        return getPop3Settings().getPort();
    }

    public void setUserName(String userName) {
        getPop3Settings().setUserName(userName);
    }


    public String getUserName(){
        return getPop3Settings().getUserName();
    }

    public void setPassword(String password) {
        getPop3Settings().setPassword(password);
    }


    public String getPassword(){
        return getPop3Settings().getPassword();
    }

    public void setSsl(boolean ssl) {
        getPop3Settings().setSsl(ssl);
    }

    public boolean isSsl(){
        return getPop3Settings().isSsl();
    }

    public void setDebug(boolean debug) {
        getPop3Settings().setDebug(debug);
    }

    public boolean isDebug(){
        return  getPop3Settings().isDebug();
    }



}
