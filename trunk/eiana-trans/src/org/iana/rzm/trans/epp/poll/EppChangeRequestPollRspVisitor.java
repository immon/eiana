package org.iana.rzm.trans.epp.poll;

import org.iana.rzm.trans.epp.EppChangeRequestPollRspVisitorException;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppChangeRequestPollRspVisitor {
    public void visitDocApproved(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitDocApprovalTimeout(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitDocRejected(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitSystemValidated(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitValidationError(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitHold(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitGenerated(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitNsRejected(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitComplete(EppPollResponse pollRsp) throws EppChangeRequestPollRspVisitorException;
}
