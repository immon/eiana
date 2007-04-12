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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @Basic
    private Timestamp created;

    @Basic
    private String type;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Addressee> addressee = new HashSet<Addressee>();

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Content content;

    @Basic
    private boolean sent;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
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

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
