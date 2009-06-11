package org.iana.rzm.web.admin.pages.editors;

import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.editors.*;
import org.iana.rzm.web.admin.services.AdminServices;


public interface UserAttributeEditor extends AttributesEditor {

    public void save(UserVOWrapper user);
    public AdminServices getAdminServices();
}
