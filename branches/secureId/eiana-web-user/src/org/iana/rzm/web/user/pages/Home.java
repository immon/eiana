package org.iana.rzm.web.user.pages;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.commons.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.user.query.retriver.*;
import org.iana.rzm.web.user.services.*;
import org.iana.web.tapestry.components.browser.*;

import java.text.*;
import java.util.*;


public abstract class Home extends UserPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "Home";
    public static final Logger LOGGER = Logger.getLogger(Home.class.getName());

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
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain",
            "parameters=components.domains.value.domainId"
        }
    )
    public abstract IComponent getDomainLinkComponent();

    @Component(id = "review", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:reviewDomain", "parameters=components.domains.value.domainId"
            }
    )
    public abstract IComponent getReviewDomainLinkComponent();

    @Component(id = "userAccess", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:manageUsers", "parameters={components.domains.value.domainId,components.domains.value.domainName}"
            }
    )
    public abstract IComponent getUserAccessLinkComponent();


    @Component(id = "listRequests", type = "rzmLib:ListRequests", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:false",
            "noRecordsMessage=literal:'There are no outstanding requests.'",
            "listener=listener:viewRequestDetails",
            "linkTragetPage=prop:reviewDomainPage",
            "cancelRequestPage=literal:WithdrawRequest"
            }
    )
    public abstract IComponent getListRequestComponent();

    @Component(id = "listImpactedpartRequests", type = "rzmLib:ImpactedPartiesListRequest", bindings = {
            "entityQuery=prop:impactedPartyEntityQuery",
            "usePagination=literal:false",
            "noRecordsMessage=literal:'There are no outstanding requests.'",
            "listener=listener:viewThirdPartyRequestDetails"
            }
    )
    public abstract IComponent getListImpactedPartyRequestComponent();



    @InjectPage(UserRequestsPerspective.PAGE_NAME)
    public abstract UserRequestsPerspective getRequestsPerspective();

    @InjectPage(RequestInformation.PAGE_NAME)
    public abstract RequestInformation getRequestDetails();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomainPage();

    @InjectPage(UserAccess.PAGE_NAME)
    public abstract UserAccess   getUserAccessPage();

    @Persist("client")
    public abstract void setUserDomains(List<UserDomain> list);
    public abstract List<UserDomain> getUserDomains();

    public abstract UserDomain getUserDomain();

    public void pageBeginRender(PageEvent event) {

        try {
            List<UserDomain> list = getUserServices().getUserDomains();
            Collections.sort(list);
            setUserDomains(list);
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListRecords) getComponent("listRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }

            count = getImpactedPartyEntityQuery().getResultCount();
            browser = ((ListRecords) getComponent("listImpactedpartRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }

        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
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
        List<String>domains = new ArrayList<String>();
        for (UserDomain domain : getUserDomains()) {
            if(!domains.contains(domain.getDomainName())){
                domains.add(domain.getDomainName());
            }
        }
        entityQuery.setFetcher(new OpenTransactionForDomainsRetriver(domains, getUserServices()));
        return entityQuery;
    }

    public EntityQuery getImpactedPartyEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        List<String>domains = new ArrayList<String>();
        for (UserDomain domain : getUserDomains()) {
            if(!domains.contains(domain.getDomainName())){
                domains.add(domain.getDomainName());
            }
        }
        entityQuery.setFetcher(new ImpactedPartyTransactionRetriver(domains, getUserServices()));
        return entityQuery;
    }

    public void viewRequestDetails(long requestId) {
        RequestInformation info = getRequestDetails();
        info.setImpactedThirdPartyView(false);
        info.setRequestId(requestId);
        info.setCallback(createCallback());
        getRequestCycle().activate(info);
    }

    public void viewThirdPartyRequestDetails(long requestId) {
        RequestInformation info = getRequestDetails();
        info.setImpactedThirdPartyView(true);
        info.setRequestId(requestId);
        info.setCallback(createCallback());
        getRequestCycle().activate(info);
    }

    public void manageUsers(long domainId, String domainName){
        UserAccess userAccess = getUserAccessPage();
        userAccess.setDomainId(domainId);
        userAccess.setDomainName(domainName);
        getRequestCycle().activate(userAccess);
    }
    
    public void viewPastRequest(){
        UserRequestsPerspective page = getRequestsPerspective();
        List<UserDomain> list = getUserDomains();
        List<String>domainNames = new ArrayList<String>();
        for (UserDomain userDomain : list) {
            domainNames.add(userDomain.getDomainName());
        }
        page.setEntityFetcher(new ClosedTransactionsForDomains(domainNames, getUserServices()));
        page.setCallback(createCallback());
        getRequestCycle().activate(page);
    }

    private static class ClosedTransactionsForDomains implements EntityRetriver {
        private UserServices services;
        private Criterion criterion;
        private SortOrder sortOrder;

        public ClosedTransactionsForDomains(List<String>domains, UserServices services) {
            criterion = QueryBuilderUtil.closeTransactionForDomains(domains);
            this.services = services;
            sortOrder = new SortOrder();
        }

        public int getTotal() throws NoObjectFoundException {
            return services.getTransactionCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return services.getTransactions(criterion, offset, length, sortOrder).toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

}
