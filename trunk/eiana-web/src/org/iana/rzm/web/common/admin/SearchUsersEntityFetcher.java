package org.iana.rzm.web.common.admin;

import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.admin.*;

import java.io.*;

public class SearchUsersEntityFetcher implements EntityFetcher, Serializable {
    private AdminServices services;
    private Criterion criterion;

    public SearchUsersEntityFetcher(AdminServices services,  Criterion criterion){
        this.services = services;
        this.criterion = criterion;
    }

    public int getTotal() throws NoObjectFoundException {
        return services.getUserCount(criterion);
    }

    public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
        return services.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
    }
}
