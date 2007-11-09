package org.iana.rzm.web.model;

import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.criteria.*;

import java.io.*;

public interface EntityFetcher extends Serializable {

    public int  getTotal() throws NoObjectFoundException;

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException;

    public void applySortOrder(SortOrder sortOrder);

}
