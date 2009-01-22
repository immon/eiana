package org.iana.rzm.web.admin.query.retriver;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

public class PollMessagesEntityRetriver implements EntityRetriver {

    private AdminServices adminServices;
    private Criterion criterion;
    private SortOrder sort;


    public PollMessagesEntityRetriver(AdminServices adminServices, Criterion criterion) {
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
