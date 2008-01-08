package org.iana.rzm.trans.epp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class EppChangeRequestPollRsp {
    public static enum Status {
        DOC_APPROVED("docApproved"),
        DOC_APPROVAL_TIMEOUT("docApprovalTimeout"),
        DOC_REJECTED("docRejected"),
        SYSTEM_VALIDATED("systemValidated"),
        VALIDATION_ERROR("validationError"),
        HOLD("Hold"),
        GENERATED("Generated"),
        NS_REJECTED("nsRejected"),
        COMPLETE("Complete");

        static Map<String, Status> namesToStatuses;

        static {
            namesToStatuses = new HashMap<String, Status>();
            for (Status st : Status.values())
                namesToStatuses.put(st.getName(), st);
        }

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Status forName(String name) {
            return namesToStatuses.get(name);
        }
    }

    private Status status;
    private String changeRequestId;

    public EppChangeRequestPollRsp(Status status, String changeRequestId) {
        this.status = status;
        this.changeRequestId = changeRequestId;
    }

    public EppChangeRequestPollRsp(String status, String changeRequestId) {
        this(Status.forName(status), changeRequestId);
    }

    public Status getStatus() {
        return status;
    }

    public String getChangeRequestId() {
        return changeRequestId;
    }

    public void accept(EppChangeRequestPollRspVisitor visitor) throws EppChangeRequestPollRspVisitorException {
        switch (status) {
            case COMPLETE:
                visitor.visitComplete(changeRequestId);
                break;
            case DOC_APPROVAL_TIMEOUT:
                visitor.visitDocApprovalTimeout(changeRequestId);
                break;
            case DOC_APPROVED:
                visitor.visitDocApproved(changeRequestId);
                break;
            case DOC_REJECTED:
                visitor.visitDocRejected(changeRequestId);
                break;
            case GENERATED:
                visitor.visitGenerated(changeRequestId);
                break;
            case HOLD:
                visitor.visitHold(changeRequestId);
                break;
            case NS_REJECTED:
                visitor.visitNsRejected(changeRequestId);
                break;
            case SYSTEM_VALIDATED:
                visitor.visitSystemValidated(changeRequestId);
                break;
            case VALIDATION_ERROR:
                visitor.visitValidationError(changeRequestId);
        }
    }
}
