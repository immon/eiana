package org.iana.rzm.trans.epp;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppChangeRequestPollRspVisitor {
    public void visitDocApproved(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitDocApprovalTimeout(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitDocRejected(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitSystemValidated(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitValidationError(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitHold(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitGenerated(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitNsRejected(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;

    public void visitComplete(EppChangeRequestPollRsp pollRsp) throws EppChangeRequestPollRspVisitorException;
}
