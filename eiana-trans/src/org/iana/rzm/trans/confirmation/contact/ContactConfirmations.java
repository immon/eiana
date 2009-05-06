package org.iana.rzm.trans.confirmation.contact;

import org.hibernate.annotations.Cascade;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.confirmation.AbstractConfirmation;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.user.SystemRole;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ContactConfirmations extends AbstractConfirmation {

    @Basic
    private TransactionState.Name stateName;

    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = ContactIdentity.class)
    @JoinColumn(name = "received_objid")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<ContactIdentity> receivedConfirmations = new HashSet<ContactIdentity>();
    @OneToMany(cascade = CascadeType.ALL,
            targetEntity = ContactIdentity.class)
    @JoinColumn(name = "pending_objid")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<ContactIdentity> outstandingConfirmations = new HashSet<ContactIdentity>();

    protected ContactConfirmations() {
    }

    public ContactConfirmations(Set<ContactIdentity> confirmations) {
        this.outstandingConfirmations.addAll(confirmations);
    }

    public boolean isAcceptableBy(ContactIdentity identity) {
        return outstandingConfirmations.contains(identity);
    }

    public boolean accept(ContactIdentity identity) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (receivedConfirmations.contains(identity))
            throw new AlreadyAcceptedByUser();
        if (!isAcceptableBy(identity))
            throw new NotAcceptableByUser();
        ContactIdentity id = findContactIdentity(identity.getToken(), outstandingConfirmations);
        assert id != null;
        outstandingConfirmations.remove(id);
        receivedConfirmations.add(id.clone());
        return isReceived();
    }

    public boolean isReceived() {
        return outstandingConfirmations.size() == 0;
    }

    public Set<ContactIdentity> getUsersAbleToAccept() {
        return new HashSet<ContactIdentity>(outstandingConfirmations);
    }

    public Set<ContactIdentity> getIdentitiesSupposedToAccept() {
        return new HashSet<ContactIdentity>(outstandingConfirmations);
    }

    public Set<ContactIdentity> getIdentitiesThatAccepted() {
        return new HashSet<ContactIdentity>(receivedConfirmations);
    }

    public Set<SystemRole.SystemType> getContactsThatAccepted() {
        Set<SystemRole.SystemType> ret = new HashSet<SystemRole.SystemType>();
        for (ContactIdentity id : receivedConfirmations) {
            ret.add(id.getType());
        }
        return ret;
    }

    public TransactionState.Name getStateName() {
        return stateName;
    }

    public void setStateName(TransactionState.Name stateName) {
        this.stateName = stateName;
    }

    public SystemRole getRoleForToken(String token) {
        CheckTool.checkNull(token, "token");
        SystemRole ret = find(token, outstandingConfirmations);
        if (ret == null) ret = find(token, receivedConfirmations);
        return ret;
    }

    private SystemRole find(String token, Set<ContactIdentity> confirmations) {
        ContactIdentity id = findContactIdentity(token, confirmations);
        return id == null ?
                null :
                new SystemRole(id.getType(), id.getDomainName());
    }

    private ContactIdentity findContactIdentity(String token, Set<ContactIdentity> confirmations) {
        if (token != null) {
            for (ContactIdentity id : confirmations) {
                if (token.equals(id.getToken())) {
                    return id;
                }
            }
        }
        return null;
    }


    public String getNamesOfIdentitiesThatAccepted() {
        StringBuilder ret = new StringBuilder();
        for (ContactIdentity id : receivedConfirmations) {
            ret.append(id.getNameOfIdentity()).append(" ");
        }
        return ret.toString();
    }

    public String getNameOfIdentity(ContactIdentity identity) {
        if (identity == null) {
            return null;
        }
        ContactIdentity found = findContactIdentity(identity.getToken(), outstandingConfirmations);
        return found == null ?
                null :
                found.getNameOfIdentity();
    }

}
