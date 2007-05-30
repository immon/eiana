package org.iana.rzm.web.pages.user;

import org.apache.log4j.Logger;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.components.Border;
import org.iana.rzm.web.components.Browser;
import org.iana.rzm.web.components.ListRequests;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.UserDomain;
import org.iana.rzm.web.services.OpenRequestFetcher;
import org.iana.rzm.web.services.PaginatedEntityQuery;
import org.iana.rzm.web.util.DateUtil;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;


public abstract class UserHome extends UserPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "user/UserHome";
    public static final Logger LOGGER = Logger.getLogger(UserHome.class.getName());

    @Component(id = "domains", type = "For", bindings = {
            "source=prop:userDomains", "value=prop:userDomain", "element=literal:tr"
            }
    )
    public abstract IComponent getForDomainComponent();

    @Component(id = "domain", type = "Insert", bindings = {"value=components.domains.value.domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "role", type = "Insert", bindings = {"value=components.domains.value.roleType"})
    public abstract IComponent getRoleComponent();

    @Component(id = "lastUpdated", type = "Insert", bindings = {"value=components.domains.value.modified"})
    public abstract IComponent getLastUpdatedComponent();

    @Component(id = "review", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain", "parameters=components.domains.value.domainId"
            }
    )
    public abstract IComponent getReviewDomainLinkComponent();

    @Component(id = "domainLink", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain",
            "parameters=components.domains.value.domainId"
        }
    )
    public abstract IComponent getDomainLinkComponent();

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:false",
            "noRequestMsg=literal:'There are no outstanding requests.'",
            "listener=listener:viewRequestDetails"
            }
    )
    public abstract IComponent getListRequestComponent();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestDetails();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    public abstract void setUserDomains(List<UserDomain> list);

    public abstract List<UserDomain> getUserDomains();

    public abstract UserDomain getUserDomain();

    public void pageBeginRender(PageEvent event) {
        List<UserDomain> list = getUserServices().getUserDomains();
        Collections.sort(list);
        setUserDomains(list);

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListRequests) getComponent("listRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }

    }

    public ReviewDomain reviewDomain(long domainId) {
        ReviewDomain page = getReviewDomainPage();
        page.setDomainId(domainId);
        return page;
    }

    public Format getFormat() {
        return new SimpleDateFormat(DateUtil.DEFAULT_PATTERN);
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(new OpenRequestFetcher(getUserServices()));
        return entityQuery;
    }

    public void viewRequestDetails(long requestId) {
        RequestInformation info = getRequestDetails();
        info.setRequestId(requestId);
        getRequestCycle().activate(info);
    }

    public IPage logout(){
        Border border = (Border) getPage().getComponent("border");
        return border.logout();
    }


}
