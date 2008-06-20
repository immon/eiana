package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.*;

@ComponentClass
public abstract class AdminRequestSummery extends RequestSummery {

    @Asset(value = "WEB-INF/admin/AdminRequestSummery.html")
    public abstract IAsset get$template();

    @Component(id = "pollMessageLink", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:viewPollMessages", "parameters=prop:request.rtId"
        })
    public abstract IComponent getPollMessagesLinkComponent();

    @Component(id = "displayPollMessages", type = "If", bindings = {"condition=prop:showPollLink"})
    public abstract IComponent getShowPollMessagesLinkComponent();

    @InjectPage(PollMessagesPerspective.PAGE_NAME)
    public abstract PollMessagesPerspective getPerspective();

    public boolean isShowPollLink() {
        return getRequest().isPartOfEPPState();
    }

    public void viewPollMessages(long rtId) {
        AdminPage page = (AdminPage) getPage();
        PollMessagesPerspective perspective = getPerspective();
        perspective.setEntityFetcher(new PollMessagesEntityFetcher(page.getAdminServices(), CriteriaBuilder.pollMessagesByRtId(rtId)));
        perspective.setCallback(page.createCallback());
        perspective.activate();
    }


}
