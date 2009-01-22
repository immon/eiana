package org.iana.rzm.trans.epp.poll;

import org.iana.rzm.trans.epp.EPPException;
import org.iana.epp.exceptions.EPPFrameworkException;

/**
 * @author Piotr Tkaczyk
 */
public interface EPPPollStatusQuery {

    public boolean queryAndProcess(EPPPollReq req) throws EPPException, EPPFrameworkException;

}
