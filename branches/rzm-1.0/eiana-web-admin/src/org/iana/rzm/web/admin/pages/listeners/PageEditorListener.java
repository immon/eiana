package org.iana.rzm.web.admin.pages.listeners;

import org.apache.tapestry.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.admin.pages.*;

import java.io.*;

public interface PageEditorListener<EntityT, EntityY extends AdminPage> extends Serializable {

    public void saveEntity(EntityY adminPage, EntityT entityT, IRequestCycle cycle, boolean checkRadicalChanges)throws NoObjectFoundException;
    public void cancel(IRequestCycle requestCycle);
}
