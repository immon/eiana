package org.iana.rzm.trans.confirmation.contact;

import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.AbstractConfirmation;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.user.SystemRole;
import org.iana.notifications.AbstractAddressee;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ContactConfirmations extends AbstractConfirmation {

    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractAddressee.class)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(name = "received_contact_confirmations",
            inverseJoinColumns = @JoinColumn(name = "received_objid"))
    private Set<ContactIdentity> receivedConfirmations = new HashSet<ContactIdentity>();
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractAddressee.class)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(name = "outstanding_contact_confirmations",
            inverseJoinColumns = @JoinColumn(name = "pending_objid"))
    private Set<ContactIdentity> outstandingConfirmations = new HashSet<ContactIdentity>();

    private ContactConfirmations() {
    }

    public ContactConfirmations(Set<ContactIdentity> confirmations) {
        this.outstandingConfirmations.addAll(confirmations);
    }

    public boolean isAcceptableBy(Identity identity) {
        return outstandingConfirmations.contains(identity);
    }

    public boolean accept(Identity identity) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!isAcceptableBy(identity))
            throw new NotAcceptableByUser();
        if (receivedConfirmations.contains(identity))
            throw new AlreadyAcceptedByUser();
        for (ContactIdentity id : outstandingConfirmations) {
            if (id != null && id.equals(identity)) {
                outstandingConfirmations.remove(id);
                receivedConfirmations.add(id.clone());
                break;
            }
        }
        return isReceived();
    }

    public boolean isReceived() {
        return outstandingConfirmations.size() == 0;
    }

    public Set<Identity> getUsersAbleToAccept() {
        return new HashSet<Identity>(outstandingConfirmations);
    }

    public Set<SystemRole.SystemType> getContactsThatAccepted() {
        Set<SystemRole.SystemType> ret = new HashSet<SystemRole.SystemType>();
        for (ContactIdentity id : receivedConfirmations) {
            ret.add((SystemRole.SystemType) id.getType());
        }
        return ret;
    }
    
}
