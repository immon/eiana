package org.iana.rzm.web.common;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: Apr 2, 2007
* Time: 1:39:27 PM
* To change this template use File | Settings | File Templates.
*/
public class OpenTransactionForDomainsFetcher implements EntityFetcher {
    private RzmServices services;
    private Criterion criterion;
    private SortOrder sortOrder;


    public OpenTransactionForDomainsFetcher(List<String> domains, RzmServices services) {
        this.services = services;
        criterion = CriteriaBuilder.openTransactionForDomains(domains);
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
