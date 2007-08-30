package org.iana.rzm.web.pages.user;

import org.apache.tapestry.annotations.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;

public abstract class UserPage extends Protected {

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();


    @InjectPage("user/GeneralError")
    public abstract GeneralError getErrorPage();

    public RzmServices getRzmServices() {
        return getUserServices();
    }

    protected void restoreCurrentDomain(long domainId) throws NoObjectFoundException {
        try {
            DomainVOWrapper wrapper = getUserServices().getDomain(domainId);
            getVisitState().markAsVisited(wrapper);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    protected void restoreModifiedDomain(DomainVOWrapper domain) throws NoObjectFoundException {
        getVisitState().markAsVisited(domain);
        TransactionActionsVOWrapper transactionActionsVOWrapper = getUserServices().getChanges(domain);
        if (transactionActionsVOWrapper.getChanges().size() > 0) {
            getVisitState().markDomainDirty(domain.getId());
        }
    }


}
