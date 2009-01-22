package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;

import java.util.*;

@ComponentClass(allowBody = true)
public abstract class ListUsers extends ListRecords {

    @Asset(value = "WEB-INF/admin/ListUsers.html")
    public abstract IAsset get$template();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    //@Component(id = "user", type = "Insert", bindings = {"value=prop:user"})
    //public abstract IComponent getUserComponent();

    @Component(id = "userName", type = "Insert", bindings = {"value=prop:record.userName"})
    public abstract IComponent getUserNameComponent();

    @Component(id = "domains", type = "Insert", bindings = {"value=prop:domains"})
    public abstract IComponent getDomainsComponent();

    @Component(id = "modified", type = "Insert", bindings = {
        "value=prop:record.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "viewUser", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:listenerParameters",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    @Component(id = "deleteUser", type = "DirectLink", bindings = {
        "listener=prop:deleteListener", "parameters=prop:listenerParameters",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getDeleteListenerComponent();

    @Component(id = "deleteEnabled", type = "If", bindings = {"condition=prop:deleteEnabled"})
    public abstract IComponent getDisplayDeleteComponent();

    @Component(id = "tooltip", type = "man:Tooltip",
               bindings = {"tooltipContent=prop:tooltipContent", "xoffset=literal:30", "yoffset=literal:-30"})
    public abstract IComponent getToolTipComponent();

    @Component(id = "tooltip1", type = "man:Tooltip",
               bindings = {"tooltipContent=prop:tooltipUserContent", "xoffset=literal:30", "yoffset=literal:-30"})
    public abstract IComponent getToolTipUserComponent();

    @InjectState("visit")
    public abstract Visit getVisit();

    @Parameter
    public abstract IActionListener getDeleteListener();

    public String getUser() {
        return getRecord().getFirstName() + " " + getRecord().getLastName();
    }

    public String getTooltipUserContent(){
        return "<span class='tooltip'> " +getUser() + " </span>";        
    }

    public String getDomains() {
        List<RoleUserDomain> list = getRecord().getUserDomains();
        if(list.size() > 1){
            return list.get(0).getDomainName() + " ...";
        }

        if(list.size() == 1){
            return list.get(0).getDomainName();
        }

        return "";

    }

    public String getTooltipContent() {

        List<RoleUserDomain> list = getRecord().getUserDomains();
        List<String>included = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (RoleUserDomain userDomain : list) {
            if(!included.contains(userDomain.getDomainName())){
                builder.append(userDomain.getDomainName()).append(" ");
                included.add(userDomain.getDomainName());
            }
        }

        return "<span class='tooltip'> " +builder.toString() + " </span>";
    }

    public boolean isDeleteEnabled() {
        return !getRecord().getUserName().equals(getVisit().getUser().getUserName());
    }

    public UserVOWrapper getRecord() {
        return (UserVOWrapper) getCurrentRecord();
    }

    public Object[] getListenerParameters() {
        return new Object[]{getRecord().getId(), getRecord().isAdmin()};
    }

}
