package org.iana.rzm.trans.epp;

import org.iana.rzm.trans.TransactionException;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppPollMsgProcessor extends EppChangeRequestPollRspVisitor {
    void visitDocApproved(String eppRequestId) throws TransactionException;

    void visitDocApprovalTimeout(String eppRequestId) throws TransactionException;

    void visitDocRejected(String eppRequestId) throws TransactionException;

    void visitSystemValidated(String eppRequestId) throws TransactionException;

    void visitValidationError(String eppRequestId) throws TransactionException;

    void visitHold(String eppRequestId) throws TransactionException;

    void visitGenerated(String eppRequestId) throws TransactionException;

    void visitNsRejected(String eppRequestId) throws TransactionException;

    void visitComplete(String eppRequestId) throws TransactionException;
}
