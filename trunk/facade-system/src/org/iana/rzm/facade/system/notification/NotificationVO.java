package org.iana.rzm.facade.system.notification;

import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationVO {
    private Long objId;
    private Set<NotificationAddresseeVO> addressees;
    private String subject;
    private String body;


    public NotificationVO(Long objId, Set<NotificationAddresseeVO> addressees, String subject, String body) {
        this.objId = objId;
        this.addressees = addressees;
        this.subject = subject;
        this.body = body;
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
}
