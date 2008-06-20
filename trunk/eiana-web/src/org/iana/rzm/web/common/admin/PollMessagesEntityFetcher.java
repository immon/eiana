package org.iana.rzm.web.common.admin;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.admin.*;

public class PollMessagesEntityFetcher implements EntityFetcher {

    private AdminServices adminServices;
    private Criterion criterion;
    private SortOrder sort;


    public PollMessagesEntityFetcher(AdminServices adminServices, Criterion criterion) {
        this.adminServices = adminServices;
        this.criterion = criterion;
        sort =new SortOrder();
    }

    public int getTotal() throws NoObjectFoundException {
        return adminServices.getPollMessagesCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return adminServices.getPollMessages(criterion,offset,length, sort).toArray(new PaginatedEntity[0]);
    }

    public void applySortOrder(SortOrder sortOrder) {
        sort = sortOrder;
    }
}
