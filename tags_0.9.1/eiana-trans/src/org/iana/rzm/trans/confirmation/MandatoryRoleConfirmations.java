package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.auth.Identity;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class MandatoryRoleConfirmations extends AbstractConfirmation {
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "MandatoryRoleConfirmations_Users",
            inverseJoinColumns = @JoinColumn(name = "RZMUser_objId"))
    private Set<RZMUser> receivedConfirmations = new HashSet<RZMUser>();
    @Basic
    private String name;
    @Enumerated
    private SystemRole.SystemType type;

    private MandatoryRoleConfirmations() {}

    public MandatoryRoleConfirmations(String name, SystemRole.SystemType type) {
        this.name = name;
        this.type = type;
    }

    public boolean isAcceptableBy(Identity user) {
        if (user instanceof RZMUser) {
            RZMUser uid = (RZMUser) user;
            return getRequiredConfirmations().contains(uid);
        }
        return false;
    }

    public boolean accept(Identity user) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!isAcceptableBy(user))
            throw new NotAcceptableByUser();
        RZMUser uid = (RZMUser) user;
        if (receivedConfirmations.contains(uid))
            throw new AlreadyAcceptedByUser();
        receivedConfirmations.add(uid);
        return isReceived();
    }

    public boolean isReceived() {
        return receivedConfirmations.containsAll(getRequiredConfirmations());
    }

    public Set<Identity> getUsersAbleToAccept() {
        return getRequiredConfirmations();
    }

    private Set<Identity> getRequiredConfirmations() {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        return new HashSet<Identity>(um.findUsersInSystemRole(name, type, true, true));
    }
}
