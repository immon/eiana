package org.iana.rzm.web.common.admin;

import org.iana.rzm.web.common.AttributesEditor;
import org.iana.rzm.web.model.UserVOWrapper;


public interface UserAttributeEditor extends AttributesEditor {

    public void save(UserVOWrapper user);
}
