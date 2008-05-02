package org.iana.rzm.trans.epp.poll;

import org.iana.rzm.trans.TransactionException;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppPollMsgProcessor extends EppChangeRequestPollRspVisitor {
    void visitDocApproved(EppPollResponse pollRsp) throws TransactionException;

    void visitDocApprovalTimeout(EppPollResponse pollRsp) throws TransactionException;

    void visitDocRejected(EppPollResponse pollRsp) throws TransactionException;

    void visitSystemValidated(EppPollResponse pollRsp) throws TransactionException;

    void visitValidationError(EppPollResponse pollRsp) throws TransactionException;

    void visitHold(EppPollResponse pollRsp) throws TransactionException;

    void visitGenerated(EppPollResponse pollRsp) throws TransactionException;

    void visitNsRejected(EppPollResponse pollRsp) throws TransactionException;

    void visitComplete(EppPollResponse pollRsp) throws TransactionException;
}
