package org.iana.rzm.web.common.query.retriver;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.components.browser.*;

import java.util.*;


public class OpenTransactionForDomainsRetriver implements EntityRetriver {
    private RzmServices services;
    private Criterion criterion;
    private SortOrder sortOrder;


    public OpenTransactionForDomainsRetriver(List<String> domains, RzmServices services) {
        this.services = services;
        criterion = QueryBuilderUtil.openTransactionForDomains(domains);
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
