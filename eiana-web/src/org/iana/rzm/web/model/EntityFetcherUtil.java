package org.iana.rzm.web.model;

import org.iana.rzm.facade.common.NoObjectFoundException;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: May 25, 2007
* Time: 10:27:58 AM
* To change this template use File | Settings | File Templates.
*/
public class EntityFetcherUtil {

    private EntityFetcher entityFetcher;
    private PaginatedEntity[] entities;

    public EntityFetcherUtil(EntityFetcher entityFetcher){

        this.entityFetcher = entityFetcher;
    }

    private PaginatedEntity[] getEntities() throws NoObjectFoundException {
        if(entities == null){
            entities = entityFetcher.getEntities();
        }

        return entities;
    }

    public PaginatedEntity[] calculatePageResult(int offset, int length) throws NoObjectFoundException {

        if (offset < 0 || length < 0) {
            return new PaginatedEntity[0];
        }

        PaginatedEntity[] entities = getEntities();

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


}
