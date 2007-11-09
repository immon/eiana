package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;

public class OpenRequestFetcher implements EntityFetcher {

    private RzmServices services;
    private SortOrder sortOrder;
    private Criterion criterion;

    public OpenRequestFetcher(RzmServices services, SortOrder sortOrder) {
        this.services = services;
        this.sortOrder = sortOrder;
        criterion = CriteriaBuilder.openTransactions();
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
