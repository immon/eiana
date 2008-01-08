package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.util.*;

public class RequestsFinderListener implements FinderListener {
    private AdminServices services;
    private IRequestCycle cycle;
    private MessageProperty messageProperty;
    private RequestsPerspective perspective;

    public RequestsFinderListener(AdminServices services, IRequestCycle cycle, MessageProperty messageProperty, RequestsPerspective perspective) {
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
            perspective.setEntityFetcher(new CachedEntityFetcher(new PaginatedEntity[]{trasanction}));
            cycle.activate(perspective);
        }

    }
}
