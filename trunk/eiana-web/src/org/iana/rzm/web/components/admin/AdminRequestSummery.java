package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;

@ComponentClass
public abstract class AdminRequestSummery extends RequestSummery {

    @Asset(value = "WEB-INF/admin/AdminRequestSummery.html")
    public abstract IAsset get$template();

    @Component(id="pollMessageLink", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:viewPollMessages", "parameters=prop:request.rtId"
        })
    public abstract IComponent getPollMessagesLinkComponent();

    @Component(id="displayPollMessages", type="If", bindings = {"condition=prop:showPollLink"})
    public abstract IComponent getShowPollMessagesLinkComponent();

    @InjectPage(PollMessagesPerspective.PAGE_NAME)
    public abstract PollMessagesPerspective getPerspective();

    public boolean isShowPollLink(){
        return getRequest().isPartOfEPPState();
    }

    public void  viewPollMessages(long rtId){
        try {
            AdminPage page = (AdminPage)getPage();
            PaginatedEntity[] messages = new PaginatedEntity[0];
            messages = page.getAdminServices().getPollMessages(rtId).toArray(new PaginatedEntity[0]);
            PollMessagesPerspective perspective = getPerspective();
            perspective.setEntityFetcher(new CachedEntityFetcher(messages));
            perspective.setCallback(page.createCallback());
            perspective.activate();
        } catch (NoObjectFoundException e) {
            AdminPage page = (AdminPage) getPage();
            page.handleNoObjectFoundException(e);
        }
    }


}
