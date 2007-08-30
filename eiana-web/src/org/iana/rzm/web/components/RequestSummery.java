package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.model.*;

public abstract class RequestSummery extends BaseComponent {

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:request.rtId"})
    public abstract IComponent getRtComponent();

    @Component(id = "state", type = "Insert", bindings = {"value=prop:request.currentStateAsString"})
    public abstract IComponent getStateComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:request.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "modified", type = "Insert", bindings = {"value=prop:request.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "createdBy", type = "Insert", bindings = {"value=prop:request.createdBy"})
    public abstract IComponent getCreatedByComponent();

    @Component(id = "requestDomain", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getRequestDomainNameComponent();

    @Component(id = "displayEditAction", type = "If", bindings = {"condition=prop:showEditLink"})
    public abstract IComponent getDisplayEditActionComponent();

    @Component(id = "displayEdit", type = "If", bindings = {"condition=prop:showEditLink"})
    public abstract IComponent getDisplayEditComponent();

    @Component(id = "edit", type = "DirectLink", bindings = {
            "listener=prop:listener", "disabled=prop:disabled",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @Parameter(required = true)
    public abstract String getDomainName();

    @Parameter(required = true)
    public abstract TransactionVOWrapper getRequest();

    @Parameter(required = false, defaultValue = "null")
    public abstract IActionListener getListener();

    public boolean isDisabled(){
        IActionListener actionListener= getListener();
        return actionListener == null;
    }

    public boolean isShowEditLink(){
        return getListener() != null;
    }

    public int getColspan(){
        return getListener() == null ? 6 : 7;
    }

}
