package org.iana.rzm.web.model;

import org.iana.rzm.facade.common.NoObjectFoundException;

public interface EntityQuery <EntityT extends PaginatedEntity> {

    public int getResultCount() throws NoObjectFoundException;

    /**
     * Returns a selected subset of the results.
     */
    public EntityT[] get(int offset, int length) throws NoObjectFoundException;
}

