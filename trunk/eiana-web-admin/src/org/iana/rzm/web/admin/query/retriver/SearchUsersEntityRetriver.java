package org.iana.rzm.web.admin.query.retriver;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

import java.io.*;

public class SearchUsersEntityRetriver implements EntityRetriver, Serializable {
    private AdminServices services;
    private Criterion criterion;

    public SearchUsersEntityRetriver(AdminServices services,  Criterion criterion){
        this.services = services;
        this.criterion = criterion;
    }

    public int getTotal() throws NoObjectFoundException {
        return services.getUserCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
    }

    public void applySortOrder(SortOrder sortOrder) {
        
    }
}
