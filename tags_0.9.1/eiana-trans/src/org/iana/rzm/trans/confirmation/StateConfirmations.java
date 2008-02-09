package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.auth.Identity;

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

    public void addConfirmation(Confirmation confirmation) {
        pendingConfirmations.add(confirmation);
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public boolean isAcceptableBy(Identity uid) {
        if (uid instanceof RZMUser) {
            if (pendingConfirmations.isEmpty() && receivedConfirmations.isEmpty())
                return true;
            RZMUser user = (RZMUser) uid;
            for (Confirmation conf : pendingConfirmations)
                if (conf.isAcceptableBy(user)) return true;
        }
        return false;
    }

    public boolean accept(Identity uid) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!pendingConfirmations.isEmpty() || !receivedConfirmations.isEmpty()) {
            RZMUser user = (RZMUser) uid;
            Set<Confirmation> userConfs = getUserConfirmations(user);
            if (userConfs.isEmpty()) throw new NotAcceptableByUser(user.getLoginName());
            for (Confirmation conf : userConfs)
                if (conf.accept(user)) {
                    pendingConfirmations.remove(conf);
                    receivedConfirmations.add(conf);
                }
        }
        return isReceived();
    }

    private Set<Confirmation> getUserConfirmations(RZMUser user) {
        Set<Confirmation> result = new HashSet<Confirmation>();
        for (Confirmation conf : pendingConfirmations)
            if (conf.isAcceptableBy(user))
                result.add(conf);
        return result;
    }

    public boolean isReceived() {
        return pendingConfirmations == null || pendingConfirmations.isEmpty();
    }

    public Set<Identity> getUsersAbleToAccept() {
        return null;
    }
}
