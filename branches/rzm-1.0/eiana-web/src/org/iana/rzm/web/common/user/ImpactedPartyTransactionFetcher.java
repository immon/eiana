package org.iana.rzm.web.common.user;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;

import java.util.*;

public class ImpactedPartyTransactionFetcher implements EntityFetcher {

    private UserServices services;
    private Criterion criterion;
    private SortOrder sortOrder;

    public ImpactedPartyTransactionFetcher(List<String> domains, UserServices services) {
        criterion = CriteriaBuilder.impactedParty(domains);
        this.services = services;
        sortOrder = new SortOrder();
    }

    public int getTotal() throws NoObjectFoundException {
        return services.getTransactionCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getTransactions(criterion, offset, length, sortOrder).toArray(new PaginatedEntity[0]);
    }

    public void applySortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

}
