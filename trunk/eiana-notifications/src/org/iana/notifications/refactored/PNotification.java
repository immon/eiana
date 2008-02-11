package org.iana.notifications.refactored;

import org.hibernate.annotations.Cascade;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class PNotification implements Serializable {

    public static final String UNKNOWN = "unknown";

    @Id
    @GeneratedValue
    Long id;

    @Basic
    private String type;

    @OneToMany(cascade = CascadeType.ALL)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "notification_id")
    private Set<PAddressee> addressees;

    @Basic
    private boolean persistent;

    @Embedded
    private PContent content;

    @Basic
    private Calendar createDate;

    @Basic
    private Calendar sentDate;

    protected PNotification() {
    }

    public PNotification(PAddressee addressee, String subject, String body) {
        CheckTool.checkNull(addressee, "addressee");
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        addressees.add(addressee);
        PContent content = new PContent(subject, body);
        init(UNKNOWN, addressees, content, false);
    }

    public PNotification(Set<PAddressee> addressees, String subject, String body) {
        PContent content = new PContent(subject, body);
        init(UNKNOWN, addressees, content, false);
    }

    public PNotification(String type, Set<PAddressee> addressees, PContent content, boolean persistent) {
        init(type, addressees, content, persistent);
    }

    private void init(String type, Set<PAddressee> addressees, PContent content, boolean persistent) {
        CheckTool.checkNull(type, "type");
        // todo: consider whether to check for empty collection or not...
        // as for now turned off
        // CheckTool.checkCollectionNullOrEmpty(addressees, "addressees");
        CheckTool.checkNull(addressees, "addressees");
        CheckTool.checkNull(content, "content");
        this.type = type;
        this.addressees = addressees;
        this.content = content;
        this.persistent = persistent;
        this.createDate = Calendar.getInstance();
        this.sentDate = null;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Set<PAddressee> getAddressees() {
        return addressees;
    }

    public PContent getContent() {
        return content;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean isSent() {
        return sentDate != null;
    }

    void markAsSent() {
        this.sentDate = Calendar.getInstance();
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public Calendar getSentDate() {
        return sentDate;
    }

}
