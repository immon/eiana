package org.iana.rzm.web.model;

import org.iana.rzm.facade.common.NoObjectFoundException;

public interface EntityFetcher {

    public int  getTotal() throws NoObjectFoundException;
    public PaginatedEntity[] getEntities() throws NoObjectFoundException;
    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException;
}
