package org.iana.rzm.web.common;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: Apr 2, 2007
* Time: 1:39:27 PM
* To change this template use File | Settings | File Templates.
*/
public class TransactionForDomainFetcher implements EntityFetcher {
    private RzmServices services;
    private Criterion criterion;


    public TransactionForDomainFetcher(String domainName, RzmServices services) {
        this.services = services;
        criterion = CriteriaBuilder.creteOpenTransactionCriterionForDomain(domainName);
    }

    public int getTotal() throws NoObjectFoundException {
        return services.getTransactionCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getTransactions(criterion, offset, length).toArray(new PaginatedEntity[0]);
    }
}
