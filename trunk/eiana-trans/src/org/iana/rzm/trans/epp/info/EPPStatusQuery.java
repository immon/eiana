package org.iana.rzm.trans.epp.info;

import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.epp.EPPException;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EPPStatusQuery {

    EPPChangeStatus queryStatusAndProcess(long transactionID) throws EPPException, TransactionException;

}
