package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.trans.change.ObjectChange;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKeyManyToMany;

import javax.persistence.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransactionData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private Long ticketID;
    @ManyToOne
    @JoinColumn(name = "currentDomain_objId")
    private Domain currentDomain;
    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "domainChange_objId")
    private ObjectChange domainChange;
    @CollectionOfElements
    @JoinTable(name = "Transaction_Confirmation_Users",
            joinColumns = @JoinColumn(name = "UserConfirmations_objId"))
    @MapKeyManyToMany
    private Map<String, UserConfirmations> userConfirmations = new HashMap<String, UserConfirmations>();

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public Domain getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Domain currentDomain) {
        this.currentDomain = currentDomain;
    }

    public ObjectChange getDomainChange() {
        return domainChange;
    }

    public void setDomainChange(ObjectChange domainChange) {
        this.domainChange = domainChange;
    }

    public void initUserConfirmations(String stateName, Set<RZMUser> impactedUsers) {
        userConfirmations.put(stateName, new UserConfirmations(impactedUsers));
    }

    public UserConfirmations getUserConfirmations(String stateName) {
        return userConfirmations.get(stateName);
    }
}
