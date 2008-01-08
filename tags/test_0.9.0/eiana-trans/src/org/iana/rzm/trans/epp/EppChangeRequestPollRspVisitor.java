package org.iana.rzm.trans.epp;

/**
 * @author Jakub Laszkiewicz
 */
public interface EppChangeRequestPollRspVisitor {
    public void visitDocApproved(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitDocApprovalTimeout(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitDocRejected(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitSystemValidated(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitValidationError(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitHold(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitGenerated(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitNsRejected(String eppRequestId) throws EppChangeRequestPollRspVisitorException;

    public void visitComplete(String eppRequestId) throws EppChangeRequestPollRspVisitorException;
}
