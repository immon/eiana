package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;

public class UsersFinderListener implements FinderListener {
    private AdminServices services;
    private IRequestCycle requestCycle;
    private MessageProperty messageProperty;
    private UsersPerspective perspective;

    public UsersFinderListener(AdminServices services, IRequestCycle requestCycle, MessageProperty messageProperty, UsersPerspective perspective) {

        this.services = services;
        this.requestCycle = requestCycle;
        this.messageProperty = messageProperty;
        this.perspective = perspective;
    }

    public void doFind(String entity) {
        UserVOWrapper user = services.getUser(entity);
        if (user == null) {
            messageProperty.setWarningMessage("Can't find user with user name: " + entity);
        } else {
            perspective.setEntityFetcher(new CachedEntityFetcher(new PaginatedEntity[]{user}));
            requestCycle.activate(perspective);
        }
    }
}
