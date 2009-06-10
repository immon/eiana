package org.iana.rzm.trans.epp.info;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
public enum EPPChangeStatus {

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

    EPPChangeStatus(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    private static Map<String, EPPChangeStatus> statuses = new HashMap<String, EPPChangeStatus>();

    static {
        for (EPPChangeStatus status : values()) {
            statuses.put(normalize(status.toString()), status);
        }
    }

    static String normalize(String status) {
        return status != null ? status.toLowerCase() : null;
    }

    public static EPPChangeStatus statusOf(String status) {
        return statuses.get(normalize(status));
    }
}
