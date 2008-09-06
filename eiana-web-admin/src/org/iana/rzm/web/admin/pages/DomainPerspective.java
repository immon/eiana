package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.admin.query.finders.*;
import org.iana.rzm.web.admin.validators.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

public abstract class DomainPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "DomainPerspective";

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @Component(id = "listDomains", type = "ListDomains", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:'There are no domains.'",
        "listener=listener:editDomain"
        }
    )
    public abstract IComponent getListRequestComponent();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomainPage();

    public FinderValidator getFinderValidator() {
        return new DomainFinderValidator();
    }

    public Finder getFinderListener() {
        return new DomainsFinderListener(getAdminServices(), getRequestCycle(), this, this);
    }

    @Persist("client")
    public abstract void setEntityFetcher(EntityRetriver entityRetriver);
    public abstract EntityRetriver getEntityFetcher();

    public void pageBeginRender(PageEvent event) {

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListDomains) getComponent("listDomains")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
        }
    }

    public void editDomain(long domainId) {
        EditDomain editDomainPage = getEditDomainPage();
        editDomainPage.setDomainId(domainId);
        getRequestCycle().activate(editDomainPage);
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = getPaginatedEntityBean();
        entityQuery.setFetcher(getEntityFetcher());
        return entityQuery;
    }


}
