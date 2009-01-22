package org.iana.rzm.web.admin.query.finders;

import org.apache.tapestry.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.query.retriver.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.feedback.*;

public class UsersFinder implements Finder {
    private AdminServices services;
    private IRequestCycle requestCycle;
    private MessageProperty messageProperty;
    private UsersPerspective perspective;
    private MessageUtil messageUtil;
    private CachedEntityRetriver fetcher;

    public UsersFinder(AdminServices services, IRequestCycle requestCycle, MessageProperty messageProperty, UsersPerspective perspective, MessageUtil messageUtil) {

        this.services = services;
        this.requestCycle = requestCycle;
        this.messageProperty = messageProperty;
        this.perspective = perspective;
        this.messageUtil = messageUtil;
    }

    public void doFind(String entity) {
        UserVOWrapper user = services.getUser(entity);
        if (user == null) {
            messageProperty.setWarningMessage(messageUtil.getUserNotFoundByUserNameErrorMessage(entity));
        } else {
            perspective.setEntityFetcher(new SearchUsersEntityRetriver(services,  QueryBuilderUtil.userName(entity)));
            requestCycle.activate(perspective);
        }
    }
 }