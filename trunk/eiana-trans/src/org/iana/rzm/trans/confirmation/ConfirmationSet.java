package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
class ConfirmationSet extends AbstractConfirmation {
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractConfirmation.class)
    @JoinTable(name = "ConfirmationSet_Confirmations",
            inverseJoinColumns = @JoinColumn(name = "ReferencedConfirmation_objId"))
    private Set<Confirmation> confirmations = new HashSet<Confirmation>();

    public ConfirmationSet() {
    }

    public ConfirmationSet(Set<Confirmation> confirmations) {
        this.confirmations.addAll(confirmations);
    }

    public void addConfirmation(Confirmation confirmation) {
        if (!confirmation.isReceived())
            confirmations.add(confirmation);
    }

    public boolean isAcceptableBy(RZMUser user) {
        if (confirmations.isEmpty()) return true;
        for (Confirmation conf : confirmations)
            if (conf.isAcceptableBy(user)) return true;
        return false;
    }

    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        for (Confirmation conf : confirmations)
            if (conf.isAcceptableBy(user))
                conf.accept(user);
        return confirmations.isEmpty();
    }

    public boolean isReceived() {
        return confirmations.isEmpty();
    }

    public Set<RZMUser> getUsersAbleToAccept() {
        Set<RZMUser> result = new HashSet<RZMUser>();
        for (Confirmation conf : confirmations)
            result.addAll(conf.getUsersAbleToAccept());
        return result;
    }
}
