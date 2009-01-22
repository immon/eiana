package org.iana.rzm.web.user.query.retriver;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.user.services.*;
import org.iana.web.tapestry.components.browser.*;

import java.util.*;

public class ImpactedPartyTransactionRetriver implements EntityRetriver {

    private UserServices services;
    private Criterion criterion;
    private SortOrder sortOrder;

    public ImpactedPartyTransactionRetriver(List<String> domains, UserServices services) {
        criterion = QueryBuilderUtil.impactedParty(domains);
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
