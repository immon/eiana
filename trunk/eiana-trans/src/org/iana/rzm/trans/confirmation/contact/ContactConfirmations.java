package org.iana.rzm.trans.confirmation.contact;

import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.AbstractConfirmation;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.auth.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ContactConfirmations extends AbstractConfirmation {

    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractConfirmation.class)
    private Set<ContactIdentity> receivedConfirmations = new HashSet<ContactIdentity>();
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = AbstractConfirmation.class)
    private Set<ContactIdentity> outstandingConfirmations = new HashSet<ContactIdentity>();

    private ContactConfirmations() {
    }

    public ContactConfirmations(Set<ContactIdentity> confirmations) {
        this.outstandingConfirmations.addAll(confirmations);
    }

    public boolean isAcceptableBy(Identity identity) {
        return outstandingConfirmations.contains(identity) || receivedConfirmations.contains(identity);
    }

    public boolean accept(Identity identity) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!isAcceptableBy(identity))
            throw new NotAcceptableByUser();
        if (receivedConfirmations.contains(identity))
            throw new AlreadyAcceptedByUser();
        outstandingConfirmations.remove(identity);
        receivedConfirmations.add((ContactIdentity) identity);
        return isReceived();
    }

    public boolean isReceived() {
        return outstandingConfirmations.size() == 0;
    }

    public Set<Identity> getUsersAbleToAccept() {
        return new HashSet<Identity>(outstandingConfirmations);
    }
}
