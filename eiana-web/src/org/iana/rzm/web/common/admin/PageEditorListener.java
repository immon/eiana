package org.iana.rzm.web.common.admin;

import org.apache.tapestry.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;

import java.io.*;

public interface PageEditorListener<EntityT> extends Serializable {

    public void saveEntity(EntityT entityT, IRequestCycle cycle) throws NoObjectFoundException, NoDomainModificationException, TransactionExistsException;

    public void cancel(IRequestCycle requestCycle);
}
