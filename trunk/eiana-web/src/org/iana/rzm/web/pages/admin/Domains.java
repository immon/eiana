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

    @InjectPage("admin/DomainPerspective")
    public abstract DomainPerspective getDomainPerspective();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomainPage();

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

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(new DomainFetcher(getAdminServices()));
        return entityQuery;
    }

    private static class DomainFetcher implements EntityFetcher {
        private AdminServices adminServices;


        public DomainFetcher(AdminServices adminServices) {
            this.adminServices = adminServices;
        }


        public int getTotal() {
            return adminServices.getDomainsCount();
        }

        public PaginatedEntity[] get(int offset, int length) {
            List<DomainVOWrapper> list = adminServices.getDomains(offset, length);
            Collections.sort(list, new Comparator<DomainVOWrapper>() {
                public int compare(DomainVOWrapper o1, DomainVOWrapper o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            return list.toArray(new PaginatedEntity[0]);
        }
    }
}
