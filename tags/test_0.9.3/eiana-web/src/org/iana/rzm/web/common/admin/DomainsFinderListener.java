package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.criteria.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public class DomainsFinderListener implements FinderListener {
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
        Criterion criterion = CriteriaBuilder.domainsByName(entity);
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
            perspective.setEntityFetcher(new CachedEntityFetcher(list.toArray(new PaginatedEntity[list.size()])));
            requestCycle.activate(perspective);
        }
    }
}
