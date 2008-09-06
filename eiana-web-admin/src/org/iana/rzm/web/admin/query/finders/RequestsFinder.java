package org.iana.rzm.web.admin.query.finders;

import org.apache.tapestry.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;
import org.iana.web.tapestry.feedback.*;

public class RequestsFinder implements Finder {

    private AdminServices services;
    private IRequestCycle cycle;
    private MessageProperty messageProperty;
    private RequestsPerspective perspective;

    public RequestsFinder(AdminServices services,
                          IRequestCycle cycle,
                          MessageProperty messageProperty,
                          RequestsPerspective perspective) {
        this.services = services;
        this.cycle = cycle;
        this.messageProperty = messageProperty;
        this.perspective = perspective;
    }


    public void doFind(String entity) {
        long rtId = Long.parseLong(entity);
        MessageUtil messageUtil = new MessageUtil();
        TransactionVOWrapper trasanction = services.getTransactionByRtId(rtId);
        if (trasanction == null) {
            messageProperty.setWarningMessage(messageUtil.getTransactionNotFoundMessage("RT ID " + entity));
        } else {
            perspective.setEntityFetcher(new CachedEntityRetriver(new PaginatedEntity[]{trasanction}));
            cycle.activate(perspective);
        }

    }
}
