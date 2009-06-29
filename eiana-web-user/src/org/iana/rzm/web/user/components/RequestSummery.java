package org.iana.rzm.web.user.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.components.*;

public abstract class RequestSummery extends BaseRequestSummery {

    @Asset(value = "WEB-INF/user/RequestSummery.html")
    public abstract IAsset get$template();

    public String getCurrentStateAsString(){
        return getRequest().getCurentUserStateAsString();
    }
    
}
