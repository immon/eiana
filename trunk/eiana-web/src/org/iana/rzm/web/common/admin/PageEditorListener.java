package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.pages.admin.*;

import java.io.*;

public interface PageEditorListener<EntityT> extends Serializable {

    public void saveEntity(AdminPage adminPage, EntityT entityT, IRequestCycle cycle) throws
                                                                 NoObjectFoundException,
                                                                 NoDomainModificationException,
                                                                 TransactionExistsException,
                                                                 NameServerChangeNotAllowedException,
                                                                 SharedNameServersCollisionException,
                                                                 RadicalAlterationException;

    public void cancel(IRequestCycle requestCycle);
}
