package org.iana.rzm.web.pages.user;

import org.apache.tapestry.annotations.InjectObject;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.TransactionActionsVOWrapper;
import org.iana.rzm.web.pages.Protected;
import org.iana.rzm.web.services.RzmServices;
import org.iana.rzm.web.services.user.UserServices;

public abstract class UserPage extends Protected {

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    public RzmServices getRzmServices(){
        return getUserServices();
    }

    protected void restoreCurrentDomain(long domainId) throws NoObjectFoundException {
        DomainVOWrapper wrapper = getUserServices().getDomain(domainId);
        getVisitState().markAsVisited(wrapper);
    }

    protected void restoreModifiedDomain(DomainVOWrapper domain) throws NoObjectFoundException {
        getVisitState().markAsVisited(domain);
        TransactionActionsVOWrapper transactionActionsVOWrapper = getUserServices().getChanges(domain);
        if (transactionActionsVOWrapper.getChanges().size() > 0) {
            getVisitState().markDomainDirty(domain.getId());
        }
    }


}
