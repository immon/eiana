package org.iana.rzm.web.common.query.retriver;

import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.web.tapestry.components.browser.*;

import java.io.*;


public class CachedEntityRetriver implements EntityRetriver, Serializable {
    private PaginatedEntity[] entities;

    public CachedEntityRetriver(PaginatedEntity[] entities) {
        this.entities = entities;
    }

    public int getTotal() throws NoObjectFoundException {
        return entities.length;
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        if (offset < 0 || length < 0) {
            return new PaginatedEntity[0];
        }

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

    public void applySortOrder(SortOrder sortOrder) {

    }
}
