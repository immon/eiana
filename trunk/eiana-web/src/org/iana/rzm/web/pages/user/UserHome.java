package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;
import org.iana.rzm.web.util.*;

import java.text.*;
import java.util.*;


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

     @Component(id = "domainLink", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain",
            "parameters=components.domains.value.domainId"
        }
    )
    public abstract IComponent getDomainLinkComponent();

    @Component(id = "review", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain", "parameters=components.domains.value.domainId"
            }
    )
    public abstract IComponent getReviewDomainLinkComponent();

    @Component(id = "userAccess", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:manageUsers", "parameters={components.domains.value.domainId,components.domains.value.domainName}"
            }
    )
    public abstract IComponent getUserAccessLinkComponent();


    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:false",
            "noRequestMsg=literal:'There are no outstanding requests.'",
            "listener=listener:viewRequestDetails",
            "linkTragetPage=prop:reviewDomainPage"
            }
    )
    public abstract IComponent getListRequestComponent();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestDetails();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @InjectPage("user/UserAccess")
    public abstract UserAccess   getUserAccessPage();

    @Persist("client:page")
    public abstract void setUserDomains(List<UserDomain> list);
    public abstract List<UserDomain> getUserDomains();

    public abstract UserDomain getUserDomain();

    public void pageBeginRender(PageEvent event) {

        try {
            List<UserDomain> list = getUserServices().getUserDomains();
            Collections.sort(list);
            setUserDomains(list);
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListRequests) getComponent("listRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
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

    public void manageUsers(long domainId, String domainName){
        UserAccess userAccess = getUserAccessPage();
        userAccess.setDomainId(domainId);
        userAccess.setDomainName(domainName);
        getRequestCycle().activate(userAccess);
    }

    public IPage logout(){
        Border border = (Border) getPage().getComponent("border");
        return border.logout();
    }

    public void viewPastRequest(){
        UserRequestsPerspective page = getRequestsPerspective();
        List<UserDomain> list = getUserDomains();
        List<String>domainNames = new ArrayList<String>();
        for (UserDomain userDomain : list) {
            domainNames.add(userDomain.getDomainName());
        }
        page.setEntityFetcher(new CloseTransactionsForDomains(domainNames, getUserServices()));
        getRequestCycle().activate(page);
    }

    private static class CloseTransactionsForDomains implements EntityFetcher{
        private UserServices services;
        private Criterion criterion;

        public CloseTransactionsForDomains(List<String>domains, UserServices services) {
            criterion = CriteriaBuilder.closeTransactionForDomains(domains);
            this.services = services;
        }

        public int getTotal() throws NoObjectFoundException {
            return services.getTransactionCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return services.getTransactions(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }
    }
}
