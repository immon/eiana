package org.iana.rzm.web.components.user;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectObject;
import org.iana.rzm.web.components.RequestDetails;
import org.iana.rzm.web.services.user.UserServices;

@ComponentClass
public abstract class UserRequestDetails extends RequestDetails {

    @Asset(value = "WEB-INF/user/UserRequestDetails.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    protected UserServices getRzmServices(){
        return getUserServices();
    }

}
