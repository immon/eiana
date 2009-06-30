package org.iana.rzm.web.admin.pages.editors;

import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.model.UserVOWrapper;
import org.iana.rzm.web.tapestry.editors.AttributesEditor;


public interface UserAttributeEditor extends AttributesEditor {

    public void save(UserVOWrapper user);
    public AdminServices getAdminServices();
}
