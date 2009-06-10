package org.iana.rzm.trans.epp.poll;

import java.util.Map;
import java.util.HashMap;

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

    private static Map<String, EppChangeStatus> statuses = new HashMap<String, EppChangeStatus>();

    static {
        for (EppChangeStatus status : EppChangeStatus.values()) {
            statuses.put(normalize(status.toString()), status);
        }
    }

    static String normalize(String status) {
        return status != null ? status.toLowerCase() : null;
    }

    public static EppChangeStatus statusOf(String status) {
        return statuses.get(normalize(status));
    }
}
