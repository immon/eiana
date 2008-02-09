package org.iana.rzm.trans.epp;

import org.iana.rzm.trans.TransactionException;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppPollMsgProcessor extends EppChangeRequestPollRspVisitor {
    void visitDocApproved(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitDocApprovalTimeout(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitDocRejected(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitSystemValidated(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitValidationError(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitHold(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitGenerated(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitNsRejected(EppChangeRequestPollRsp pollRsp) throws TransactionException;

    void visitComplete(EppChangeRequestPollRsp pollRsp) throws TransactionException;
}
