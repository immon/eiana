package org.iana.rzm.web.user.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;

public abstract class UserImpactedpartiesRequestDetails extends RequestDetails {

    @Asset(value = "WEB-INF/user/UserImpactedpartiesRequestDetails.html")
    public abstract IAsset get$template();
        
    public String getDomainName() {
        Set<String> domains = getRequest().getImpactedDomains();
        List<RoleUserDomain> list = getUser().getUserDomains();
        for (RoleUserDomain userDomain : list) {
            String domainName = userDomain.getDomainName();
            if(domains.contains(domainName)){
                return userDomain.getDomainName();
            }
        }
        return "";
    }

    public boolean isActionEnabled() {
        TransactionVOWrapper wrapper = getRequest();

        boolean result = true;

        //if(getUser().isTc() && (!wrapper.tcConfirmed())){
        //    result = true;
        //}
        //
        //if(getUser().isAc() && (!wrapper.acConfirmed())){
        //    result = true;
        //}

        return wrapper.getState().equals(TransactionStateVOWrapper.State.PENDING_IMPACTED_PARTIES) && result;
    }

    public List<ConfirmationVOWrapper>getConfirmations(){
        return getRequest().getImpactedPartiesConfirmations();
    }
}
