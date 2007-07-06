package org.iana.rzm.web.services;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;

public class PaginatedEntityQuery implements EntityQuery {

    private EntityFetcher entityFetcher;

    public int getResultCount() throws NoObjectFoundException {
        return entityFetcher.getTotal();
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return entityFetcher.get(offset, length);
    }

    public void setFetcher(EntityFetcher fetcher) {
        this.entityFetcher = fetcher;
    }
}
