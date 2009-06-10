package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;

public abstract class ViewPollMessage extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "admin/ViewPollMessage";

    @Component(id = "domainHeader",
               type = "DomainHeader",
               bindings = {"domainName=prop:domainName", "countryName=prop:country"})
    public abstract IComponent getDomainHeaderComponent();

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:rtId"})
    public abstract IComponent getRtComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "created", type = "Insert", bindings = "value=prop:created")
    public abstract IComponent getCreatedComponent();

    @Component(id = "messageId", type = "Insert", bindings = {"value=prop:messageId"})
    public abstract IComponent getMessageIdComponent();

    @Component(id = "message", type = "InsertText", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "delete", type = "DirectLink", bindings = {
        "listener=listener:delete", "parameters=prop:eppMessageId", "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getDeleteComponent();

    @Component(id = "back", type = "DirectLink", bindings = {
        "listener=listener:back", "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getBackComponent();

    @InjectPage(PollMessagesPerspective.PAGE_NAME)
    public abstract PollMessagesPerspective getPollMessagesPerspective();

    @Persist("client")
    public abstract long getEppMessageId();
    public abstract void setEppMessageId(long id);


    public abstract void setPollMessage(PollMessageVOWrapper wrapper);
    public abstract PollMessageVOWrapper getPollMessage();

    public abstract void setCountry(String country);


    protected Object[] getExternalParameters() {
        return new Object[]{getEppMessageId()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        String idAsString = parameters[0].toString();
        Long id = Long.parseLong(idAsString);
        setEppMessageId(id);
    }

    public void pageBeginRender(PageEvent event) {
        if (getPollMessage() == null) {
            try {
                setPollMessage(getAdminServices().getPollMessage(getEppMessageId()));
            } catch (NoObjectFoundException e) {
                getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
            }
            setCountry(getDomainName() == null ? "Not Availabe" : getAdminServices().getCountryName(getDomainName()));
        }
    }

    public void delete(long id) {
        try {
            getAdminServices().deletePollMessage(id);
            back();
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public void back() {
        PollMessagesPerspective page = getPollMessagesPerspective();
        page.activate();
    }

    public long getRtId() {
        return getPollMessage().getRtId();
    }

    public String getDomainName() {
        return getPollMessage().getDomainName();
    }

    public String getCreated() {
        return getPollMessage().getCreated();
    }

    public String getMessageId() {
        return getPollMessage().getMessageId();
    }

    public String getMessage() {
        return getPollMessage().getMessage();
    }


}
