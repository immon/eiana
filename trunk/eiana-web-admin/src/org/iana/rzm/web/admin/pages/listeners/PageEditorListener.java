package org.iana.rzm.web.admin.pages.listeners;

import org.apache.tapestry.IRequestCycle;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.admin.pages.AdminPage;

import java.io.Serializable;

public interface PageEditorListener<EntityT, EntityY extends AdminPage> extends Serializable {

    public void saveEntity(EntityY adminPage, EntityT entityT, IRequestCycle cycle, boolean checkRadicalChanges)throws NoObjectFoundException;
    public void cancel(IRequestCycle requestCycle);
}
