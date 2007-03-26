package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents all confirmations required to obtain in a process state.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class StateConfirmations implements Confirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractConfirmation.class)
    @JoinTable(name = "StateConfirmations_receivedConfirmations",
            inverseJoinColumns = @JoinColumn(name = "Confirmation_objId"))
    private Set<Confirmation> receivedConfirmations = new HashSet<Confirmation>();
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractConfirmation.class)
    @JoinTable(name = "StateConfirmations_pendingConfirmations",
            inverseJoinColumns = @JoinColumn(name = "Confirmation_objId"))
    private Set<Confirmation> pendingConfirmations = new HashSet<Confirmation>();
    @Basic
    private String state;

    public void addConfirmation(Confirmation confirmation) {
        pendingConfirmations.add(confirmation);
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public boolean isAcceptableBy(RZMUser user) {
        for (Confirmation conf : pendingConfirmations)
            if (conf.isAcceptableBy(user)) return true;
        return false;
    }

    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        Set<Confirmation> userConfs = getUserConfirmations(user);
        for (Confirmation conf : userConfs)
            if (conf.accept(user)) {
                pendingConfirmations.remove(conf);
                receivedConfirmations.add(conf);
            }
        return isReceived();
    }

    private Set<Confirmation> getUserConfirmations (RZMUser user) {
        Set<Confirmation> result = new HashSet<Confirmation>();
        for (Confirmation conf : pendingConfirmations)
            if (conf.isAcceptableBy(user))
                result.add(conf);
        return result;
    }

    public boolean isReceived() {
        return pendingConfirmations == null || pendingConfirmations.isEmpty();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
