package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.admin.query.finders.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.admin.validators.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

import java.util.*;


public abstract class Domains extends AdminPage implements PageBeginRenderListener, Search {

    public static final String PAGE_NAME = "Domains";

    @Component(id = "listDomains", type = "ListDomains", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:'There are no requests.'",
        "listener=listener:editDomain"
        }
    )
    public abstract IComponent getListRequestComponent();

    @Component(id = "createNew", type = "DirectLink", bindings = { "listener=listener:createNew",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCreateNewComponent();

    @InjectPage(DomainPerspective.PAGE_NAME)
    public abstract DomainPerspective getDomainPerspective();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomainPage();

    @InjectPage(NewDomainSelection.PAGE_NAME)
    public abstract NewDomainSelection getNewDomainSelectionPage();
        
    public FinderValidator getFinderValidator() {
        return new DomainFinderValidator();
    }

    public Finder getFinderListener() {
        return new DomainsFinderListener(getAdminServices(), getRequestCycle(), this, getDomainPerspective());
    }


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

    public void editDomain(long domainId){
        EditDomain editDomainPage = getEditDomainPage();
        editDomainPage.setDomainId(domainId);
        editDomainPage.setCallback(createCallback());
        getRequestCycle().activate(editDomainPage);
    }

    public void createNew(){
        NewDomainSelection page = getNewDomainSelectionPage();
        page.setCallback(createCallback());
        getRequestCycle().activate(page);
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(new DomainRetriver(getAdminServices()));
        return entityQuery;
    }

    private static class DomainRetriver implements EntityRetriver {
        private AdminServices adminServices;
        private SortOrder sortOrder;

        public DomainRetriver(AdminServices adminServices) {
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
