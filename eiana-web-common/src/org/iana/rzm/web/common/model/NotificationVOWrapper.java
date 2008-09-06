package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.system.notification.*;

import java.io.*;

public class NotificationVOWrapper implements Serializable {

    public static enum Type {
        CONTACT_CONFIRMATION("Contact Confirmation"),
        CONTACT_CONFIRMATION_REMAINDER("Contact Confirmation Reminder"),
        IMPACTED_PARTIES_CONFIRMATION("Impacted Parties Confirmation"),
        IMPACTED_PARTIES_CONFIRMATION_REMAINDER("Impacted Parties Confirmation Reminder"),
        USDOC_CONFIRMATION("DoC Confirmation"),
        USDOC_CONFIRMATION_REMAINDER("DoC Confirmation Reminder"),
        COMPLETED("Completed Message"),
        REJECTED("Rejected Message"),
        WITHDRAWN("Withdrawn Message"),
        ADMIN_CLOSED("Admin Close Message"),
        EXCEPTION("Exception Message"),
        FAILED_TECHNICAL_CHECK_PERIOD("Failed Technical check period"),
        FAILED_TECHNICAL_CHECK("Failed Technical check"),
        TEXT("Free Form");

        private String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Type fromVoType(NotificationVO.Type type) {
            return Type.values()[type.ordinal()];
        }

        public NotificationVO.Type voType(){
            return NotificationVO.Type.values()[this.ordinal()]; 
        }

    }

    private NotificationVO vo;

    public NotificationVOWrapper(NotificationVO vo) {
        this.vo = vo;
    }

    public long getNotificationId() {
        return vo.getObjId();
    }

    public Type getType() {
        return Type.fromVoType(vo.getType());
    }

    public String getTypeAsString() {
        return getType().getDisplayName();
    }


}
