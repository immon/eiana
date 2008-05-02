package org.iana.rzm.trans.epp.poll;

/**
 * @author Patrycja Wegrzynowicz
 */
public enum EppChangeStatus {

    initial(1),
    submitted(2),
    docApproved(3),
    systemValidated(4),
    hold(5),
    nsVerified(6),
    dbUpdated(7),
    generated(8),
    locallyTested(9),
    pushed(10),
    aRootTested(11),
    complete(12),
    // error states
    docApprovalTimeout(-1),
    docRejected(0),
    validationError(-1),
    nsRejected(-1),
    // exception states
    zoneGenFailure(-1),
    zoneValidationFailed(-1),
    pushFailure(-1),
    aRootTestFailure(-1);

    int orderNumber;

    EppChangeStatus(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }
}
