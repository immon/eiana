package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;

public abstract class DomainPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/DomainPerspective";

    @Component(id = "listDomains", type = "ListDomains", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:'There are no domains.'",
        "listener=listener:editDomain"
        }
    )
    public abstract IComponent getListRequestComponent();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomainPage();

    public FinderValidator getFinderValidator() {
        return new DomainFinderValidator();
    }

    public FinderListener getFinderListener() {
        return new DomainsFinderListener(getAdminServices(), getRequestCycle(), this, this);
    }


    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @Persist("client")
    public abstract void setEntityFetcher(EntityFetcher entityFetcher);
    public abstract EntityFetcher getEntityFetcher();

    public void pageBeginRender(PageEvent event) {

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListDomains) getComponent("listDomains")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

     public void editDomain(long domainId){
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
