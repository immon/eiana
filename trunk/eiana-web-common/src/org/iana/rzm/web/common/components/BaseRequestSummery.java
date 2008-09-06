package org.iana.rzm.web.common.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;


public abstract class BaseRequestSummery extends BaseComponent {

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:request.rtIdAsString"})
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

     @Component(id="domainLink", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:goToDomain", "parameters=prop:domainName"
        })
    public abstract IComponent getDomainLinkComponent();

    @Component(id = "displayLinkActionLabel", type = "If", bindings = {"condition=prop:showActionLink"})
    public abstract IComponent getDisplayLinkActionComponent();

    @Component(id = "displayAction", type = "If", bindings = {"condition=prop:showActionLink"})
    public abstract IComponent getDisplayEditActionComponent();

    @Component(id = "displayEdit", type = "If", bindings = {"condition=prop:showEditLink"})
    public abstract IComponent getDisplayEditComponent();

    @Component(id = "displayConfirmationSender", type = "If", bindings = {"condition=prop:showSendConfirmationLink"})
    public abstract IComponent getDisplayConfirmationSender();

    @Component(id = "edit", type = "DirectLink", bindings = {
            "listener=prop:listener", "disabled=prop:disabled",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @Component(id = "send", type = "DirectLink", bindings = {
                "listener=prop:confirmationSenderListener",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
        public abstract IComponent getResendComponent();

    @Component(id="spacer", type="Image", bindings = {"image=prop:spacer"})
    public abstract IComponent getSpacerComponent();

    @Asset("images/spacer.png")
    public abstract IAsset getSpacer();

    @Parameter(required = true)
    public abstract String getDomainName();

    @Parameter(required = true)
    public abstract TransactionVOWrapper getRequest();

    @Parameter(required = true)
    public abstract LinkTraget getLinkTragetPage();

    @Parameter(required = false, defaultValue = "null")
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "null")
    public abstract IActionListener getConfirmationSenderListener();

    public boolean isDisabled(){
        IActionListener actionListener= getListener();
        return actionListener == null;
    }

    public boolean isShowEditLink(){
        return getListener() != null;
    }

    public boolean isShowSendConfirmationLink(){
        return getConfirmationSenderListener() != null;
    }

    public boolean isShowActionLink(){
        return getListener() != null || getConfirmationSenderListener() != null;
    }

    public int getColspan(){
        return getListener() == null && getConfirmationSenderListener() ==null ? 6 : 7;
    }

    public void goToDomain(){
        LinkTraget target = getLinkTragetPage();
        target.setIdentifier(getDomainName());
        getPage().getRequestCycle().activate(target);
    }

}
