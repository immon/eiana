package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Jul 19, 2007
 * Time: 10:54:17 AM
 * To change this template use File | Settings | File Templates.
 */
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

    public void doFind(String entity) {
        DomainVOWrapper domain = null;
        try {
            domain = services.getDomain(entity);
            if (domain == null) {
                messageProperty.setWarningMessage("Can't find a domain with domain name: " + entity);
            } else {
                perspective.setEntityFetcher(new CachedEntityFetcher(new PaginatedEntity[]{domain}));
                requestCycle.activate(perspective);
            }
        } catch (NoObjectFoundException e) {
            messageProperty.setWarningMessage("Can't find a domain with domain name: " + entity);
        }

    }
}
