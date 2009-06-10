package org.iana.rzm.web.admin.query.retriver;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.components.browser.*;

public class OpenRequestRetriver implements EntityRetriver {

    private RzmServices services;
    private SortOrder sortOrder;
    private Criterion criterion;

    public OpenRequestRetriver(RzmServices services, SortOrder sortOrder) {
        this.services = services;
        this.sortOrder = sortOrder;
        criterion = QueryBuilderUtil.openTransactions();
    }


    public int getTotal() throws NoObjectFoundException {
        return services.getTransactionCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getTransactions(criterion, offset, length,sortOrder).toArray(new PaginatedEntity[0]);
    }

    public void applySortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
