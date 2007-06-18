package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.components.Browser;
import org.iana.rzm.web.components.admin.ListDomains;
import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.services.PaginatedEntityQuery;
import org.iana.rzm.web.services.admin.AdminServices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class Domains extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/Domains";

    @Component(id = "listDomains", type = "ListDomains", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:'There are no requests.'",
            "listener=listener:viewRequestDetails"
            }
    )
    public abstract IComponent getListRequestComponent();


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

        public PaginatedEntity[] getEntities() throws NoObjectFoundException {
            List<DomainVOWrapper> proposals = adminServices.getDomains();
            Collections.sort(proposals, new Comparator<DomainVOWrapper>() {
                public int compare(DomainVOWrapper o1, DomainVOWrapper o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            return proposals.toArray(new PaginatedEntity[0]);
        }

        public PaginatedEntity[] get(int offset, int length) {
            List<DomainVOWrapper> list = adminServices.getDomains(offset, length);
            return  list.toArray(new PaginatedEntity[0]);
        }
    }
}
