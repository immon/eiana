package org.iana.notifications;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
@Entity
public class Notification {
    private static final String TYPE_TEXT = "text";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @Basic
    private Timestamp created;

    @Basic
    private String type;

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = AbstractAddressee.class)
    private Set<Addressee> addressee = new HashSet<Addressee>();

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Content content;

    @Basic
    private boolean sent;

    @Basic
    private long sentFailures;

    @Basic
    private boolean persistent;

    @Basic
    private Long transactionId;

    @Basic
    private Long ticketId;

    public Notification() {
        this(null, null);
    }

    public Notification(Long transactionId, Long ticketId) {
        this.created = new Timestamp(System.currentTimeMillis());
        this.sent = false;
        this.sentFailures = 0;
        this.transactionId = transactionId;
        this.ticketId = ticketId;
        this.persistent = false;
    }

    public Long getObjId() {
        return objId;
    }

    public long getSentFailures() {
        return sentFailures;
    }

    public void incSentFailures() {
        this.sentFailures++;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Addressee> getAddressee() {
        return this.addressee;
    }

    public void setAddressee(Set<Addressee> addressee) {
        this.addressee = addressee;
    }

    public void addAddressee(Addressee addressee) {
        this.addressee.add(addressee);
    }

    public void addAllAddressees(Set<Addressee> addressees) {
        this.addressee.addAll(addressees);
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
        if (content.isTemplateContent())
            type = ((TemplateContent) content).getTemplateName();
        else
            type = TYPE_TEXT; 
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getTicketId() {
        return ticketId;
    }
}