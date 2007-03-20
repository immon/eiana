package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.user.RZMUser;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKeyManyToMany;

import javax.persistence.*;
import java.util.List;
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
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Trasaction_Actions",
            inverseJoinColumns = @JoinColumn(name = "Action_objId"))
    private List<TransactionAction> actions;
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

    public List<TransactionAction> getActions() {
        return actions;
    }

    public void setActions(List<TransactionAction> actions) {
        this.actions = actions;
    }

    public void initUserConfirmations(String stateName, Set<RZMUser> impactedUsers) {
        userConfirmations.put(stateName, new UserConfirmations(impactedUsers));
    }

    public UserConfirmations getUserConfirmations(String stateName) {
        return userConfirmations.get(stateName);
    }
}
