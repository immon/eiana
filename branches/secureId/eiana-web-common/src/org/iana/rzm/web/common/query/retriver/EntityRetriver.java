package org.iana.rzm.web.common.query.retriver;

import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.web.tapestry.components.browser.*;

import java.io.*;

public interface EntityRetriver extends Serializable {

    public int  getTotal() throws NoObjectFoundException;

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException;

    public void applySortOrder(SortOrder sortOrder);

}
