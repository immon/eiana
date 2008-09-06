package org.iana.rzm.web.admin.query.finders;

import org.apache.tapestry.*;
import org.iana.commons.*;
import org.iana.criteria.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;
import org.iana.web.tapestry.feedback.*;

import java.util.*;

public class DomainsFinderListener implements Finder {

    private MessageProperty messageProperty;
    private AdminServices services;
    private IRequestCycle requestCycle;
    private DomainPerspective perspective;

    public DomainsFinderListener(AdminServices services,
                                 IRequestCycle requestCycle,
                                 MessageProperty messageProperty,
                                 DomainPerspective perspective) {

        this.messageProperty = messageProperty;
        this.services = services;
        this.requestCycle = requestCycle;
        this.perspective = perspective;
    }

    public void doFind(final String entity) {
        Criterion criterion = QueryBuilderUtil.domainsByName(entity);
        List<DomainVOWrapper> list = services.getDomains(criterion);
        if (list == null || list.size() == 0) {
            list = services.getDomains(null);
            ListUtil.filter(list, new ListUtil.Predicate<DomainVOWrapper>() {
                public boolean evaluate(DomainVOWrapper object) {
                    return object.getName().startsWith(entity);
                }
            });
        }

        if (list == null || list.size() == 0) {
            messageProperty.setErrorMessage("Can't find domain with name " + entity);
        } else {
            perspective.setEntityFetcher(new CachedEntityRetriver(list.toArray(new PaginatedEntity[list.size()])));
            requestCycle.activate(perspective);
        }
    }
}
