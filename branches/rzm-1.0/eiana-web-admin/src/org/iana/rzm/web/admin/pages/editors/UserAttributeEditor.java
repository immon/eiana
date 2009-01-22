package org.iana.rzm.web.admin.pages.editors;

import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.editors.*;


public interface UserAttributeEditor extends AttributesEditor {

    public void save(UserVOWrapper user);
}
