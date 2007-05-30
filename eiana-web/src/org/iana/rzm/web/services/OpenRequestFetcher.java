package org.iana.rzm.web.services;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityFetcherUtil;
import org.iana.rzm.web.model.PaginatedEntity;

public class OpenRequestFetcher implements EntityFetcher {

    private RzmServices services;
    private EntityFetcherUtil entityFetcherUtil;

    public OpenRequestFetcher(RzmServices services) {
        this.services = services;
        entityFetcherUtil = new EntityFetcherUtil(this);
    }


    public int getTotal() throws NoObjectFoundException {
        return services.getOpenTransaction().size();
    }

    public PaginatedEntity[] getEntities() throws NoObjectFoundException {
        return services.getOpenTransaction().toArray(new PaginatedEntity[0]);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
       return entityFetcherUtil.calculatePageResult(offset, length);
    }
}
