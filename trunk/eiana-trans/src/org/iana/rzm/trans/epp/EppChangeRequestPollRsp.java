package org.iana.rzm.trans.epp;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private boolean messageAwaiting;
    private String message;
    private List<String> errors = new ArrayList<String>();

    public EppChangeRequestPollRsp(Status status, String changeRequestId, boolean messageAwaiting, String message, List<String> errors) {
        this.status = status;
        this.changeRequestId = changeRequestId;
        this.messageAwaiting = messageAwaiting;
        this.message = message;
        this.errors.addAll(errors);
    }

    public EppChangeRequestPollRsp(String status, String changeRequestId, boolean messageAwaiting, String message, List<String> errors) {
        this(Status.forName(status), changeRequestId, messageAwaiting, message, errors);
    }

    public Status getStatus() {
        return status;
    }

    public String getChangeRequestId() {
        return changeRequestId;
    }

    public boolean isMessageAwaiting() {
        return messageAwaiting;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void accept(EppChangeRequestPollRspVisitor visitor) throws EppChangeRequestPollRspVisitorException {
        if (messageAwaiting) {
            if (status == null)
                Logger.getLogger(EppChangeRequestPollRsp.class).warn("status is null");
            else
                switch (status) {
                    case COMPLETE:
                        visitor.visitComplete(this);
                        break;
                    case DOC_APPROVAL_TIMEOUT:
                        visitor.visitDocApprovalTimeout(this);
                        break;
                    case DOC_APPROVED:
                        visitor.visitDocApproved(this);
                        break;
                    case DOC_REJECTED:
                        visitor.visitDocRejected(this);
                        break;
                    case GENERATED:
                        visitor.visitGenerated(this);
                        break;
                    case HOLD:
                        visitor.visitHold(this);
                        break;
                    case NS_REJECTED:
                        visitor.visitNsRejected(this);
                        break;
                    case SYSTEM_VALIDATED:
                        visitor.visitSystemValidated(this);
                        break;
                    case VALIDATION_ERROR:
                        visitor.visitValidationError(this);
                }
        }
    }
}
