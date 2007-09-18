package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.notification.*;

public class NotificationVOWrapper {
    private NotificationVO vo;

    public NotificationVOWrapper(NotificationVO vo) {
        this.vo = vo;
    }

    public long getNotificationId(){
        return vo.getObjId();
    }

    public String getType() {
        return "NASK NEED TO IMPlements this";
    }
}
