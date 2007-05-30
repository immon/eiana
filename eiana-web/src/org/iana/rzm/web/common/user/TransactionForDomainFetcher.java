package org.iana.rzm.web.common.user;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityFetcherUtil;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.services.user.UserServices;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: Apr 2, 2007
* Time: 1:39:27 PM
* To change this template use File | Settings | File Templates.
*/
public class TransactionForDomainFetcher implements EntityFetcher {
    private String domainName;
    private UserServices userServices;
    private EntityFetcherUtil entityFetcherUtil;


    public TransactionForDomainFetcher(String domainName, UserServices userServices) {
        this.domainName = domainName;
        this.userServices = userServices;
        entityFetcherUtil = new EntityFetcherUtil(this);
    }

    public int getTotal() throws NoObjectFoundException {
        return userServices.getOpenTransactionsForDomin(domainName).size();
    }

    public PaginatedEntity[] getEntities() throws NoObjectFoundException {
        return userServices.getOpenTransactionsForDomin(domainName).toArray(new PaginatedEntity[0]);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return entityFetcherUtil.calculatePageResult(offset, length);
    }
}
