package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.annotations.InjectObject;
import org.iana.rzm.web.pages.Protected;
import org.iana.rzm.web.services.RzmServices;
import org.iana.rzm.web.services.admin.AdminServices;

public abstract class AdminPage extends Protected {

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    public RzmServices getRzmServices(){
        return getAdminServices();
    }
}
