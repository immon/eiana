package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

public abstract class CreateDomain extends AdminPage implements DomainAttributeEditor {

    @Persist("client")
    public abstract long getDomainId();

    public abstract void setDomain(SystemDomainVOWrapper domain);


    public void save() {

    }

    public void revert() {

    }

}
