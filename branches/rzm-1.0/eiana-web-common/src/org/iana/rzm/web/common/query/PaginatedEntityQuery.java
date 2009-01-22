package org.iana.rzm.web.common.query;

import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

public class  PaginatedEntityQuery implements EntityQuery {

    private EntityRetriver entityRetriver;

    public int getResultCount() throws QueryException {
        try {
            return entityRetriver.getTotal();
        } catch (NoObjectFoundException e) {
            throw new QueryException(e);
        }
    }

    public PaginatedEntity[] get(int offset, int length) throws QueryException {
        try {
            return entityRetriver.get(offset, length);
        } catch (NoObjectFoundException e) {
            throw new QueryException(e);
        }
    }

    public void setFetcher(EntityRetriver retriver) {
        this.entityRetriver = retriver;
    }
}
