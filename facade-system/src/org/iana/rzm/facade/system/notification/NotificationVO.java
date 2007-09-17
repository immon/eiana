package org.iana.rzm.facade.system.notification;

import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationVO {
    public static enum Type {
        CONTACT_CONFIRMATION,
        CONTACT_CONFIRMATION_REMAINDER,
        IMPACTED_PARTIES_CONFIRMATION,
        IMPACTED_PARTIES_CONFIRMATION_REMAINDER,
        USDOC_CONFIRMATION,
        USDOC_CONFIRMATION_REMAINDER,
        ZONE_INSERTION_ALERT,
        ZONE_PUBLICATION_ALERT,
        COMPLETED,
        REJECTED,
        WITHDRAWN,
        ADMIN_CLOSED,
        EXCEPTION,
        FAILED_TECHNICAL_CHECK_PERIOD,
        FAILED_TECHNICAL_CHECK,
        TEXT
    }

    private Long objId;
    private Set<NotificationAddresseeVO> addressees;
    private String subject;
    private String body;
    private Type type;
    //private Timestamp created;
    //private boolean sent;
    //private long sentFailures;
    //private boolean persistent;
    //private Long transactionId;

    public NotificationVO(Long objId, Set<NotificationAddresseeVO> addressees, String subject, String body, Type type) {
        this.objId = objId;
        this.addressees = addressees;
        this.subject = subject;
        this.body = body;
        this.type = type;
    }

    public Long getObjId() {
        return objId;
    }

    public Set<NotificationAddresseeVO> getAddressees() {
        return addressees;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Type getType() {
        return type;
    }
}
