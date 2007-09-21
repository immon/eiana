package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;

public class OpenRequestFetcher implements EntityFetcher {

    private RzmServices services;
    private Criterion criterion;

    public OpenRequestFetcher(RzmServices services) {
        this.services = services;
        criterion = CriteriaBuilder.openTransactions();
    }


    public int getTotal() throws NoObjectFoundException {
        return services.getTransactionCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getTransactions(criterion, offset, length).toArray(new PaginatedEntity[0]);
    }
}
