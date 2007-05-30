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

    public PaginatedEntity[] calculatePageResult(int offset, int length) throws NoObjectFoundException {

        if (offset < 0 || length < 0) {
            return new PaginatedEntity[0];
        }

        PaginatedEntity[] entities = entityFetcher.getEntities();

        if (entities == null || offset > entities.length) {
            return new PaginatedEntity[0];
        }

        if (offset + length <= entities.length) {
            PaginatedEntity[] result = new PaginatedEntity[length];
            System.arraycopy(entities, offset, result, 0, length);
            return result;
        } else {
            int size = entities.length - offset;
            PaginatedEntity[] result = new PaginatedEntity[size];
            System.arraycopy(entities, offset, result, 0, size);
            return result;
        }
    }

    public void setFetcher(EntityFetcher fetcher) {
        this.entityFetcher = fetcher;
    }
}
