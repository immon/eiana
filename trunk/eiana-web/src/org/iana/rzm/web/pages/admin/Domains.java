package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;

import java.util.*;


public abstract class Domains extends AdminPage implements PageBeginRenderListener, Search {

    public static final String PAGE_NAME = "admin/Domains";

    @Component(id = "listDomains", type = "ListDomains", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:'There are no requests.'",
        "listener=listener:editDomain"
        }
    )
    public abstract IComponent getListRequestComponent();

    @Component(id = "createNew", type = "DirectLink", bindings = { "listener=listener:createNew",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCreateNewComponent();

    @InjectPage("admin/DomainPerspective")
    public abstract DomainPerspective getDomainPerspective();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomainPage();

    @InjectPage(NewDomainSelection.PAGE_NAME)
    public abstract NewDomainSelection getNewDomainSelectionPage();

    public FinderValidator getFinderValidator() {
        return new DomainFinderValidator();
    }

    public FinderListener getFinderListener() {
        return new DomainsFinderListener(getAdminServices(), getRequestCycle(), this, getDomainPerspective());
    }


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

    public void createNew(){
        NewDomainSelection page = getNewDomainSelectionPage();
        getRequestCycle().activate(page);
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(new DomainFetcher(getAdminServices()));
        return entityQuery;
    }

    private static class DomainFetcher implements EntityFetcher {
        private AdminServices adminServices;
        private SortOrder sortOrder;

        public DomainFetcher(AdminServices adminServices) {
            this.adminServices = adminServices;
            sortOrder = new SortOrder();
        }


        public int getTotal() {
            return adminServices.getDomainsCount();
        }

        public PaginatedEntity[] get(int offset, int length) {
            List<DomainVOWrapper> list = adminServices.getDomains(offset, length, sortOrder);
            return list.toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }
    }
}
