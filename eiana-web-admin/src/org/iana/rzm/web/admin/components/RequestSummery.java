package org.iana.rzm.web.admin.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.query.*;
import org.iana.rzm.web.admin.query.retriver.*;
import org.iana.rzm.web.common.components.*;

@ComponentClass
public abstract class RequestSummery extends BaseRequestSummery {

    @Asset(value = "WEB-INF/admin/RequestSummery.html")
    public abstract IAsset get$template();
    
    @Component(id = "pollMessageLink", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
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
        perspective.setEntityFetcher(new PollMessagesEntityRetriver(page.getAdminServices(), AdminQueryUtil.pollMessagesByRtId(rtId)));
        perspective.setCallback(page.createCallback());
        perspective.activate();
    }


}
