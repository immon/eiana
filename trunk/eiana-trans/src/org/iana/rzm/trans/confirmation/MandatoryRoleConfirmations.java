package org.iana.rzm.trans.confirmation;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
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
    @OneToMany(cascade = CascadeType.ALL)
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

    public boolean isAcceptableBy(RZMUser user) {
        return getRequiredConfirmations().contains(user);
    }

    public boolean accept(RZMUser user) throws AlreadyAcceptedByUser, NotAcceptableByUser {
        if (!isAcceptableBy(user))
            throw new NotAcceptableByUser();
        if (receivedConfirmations.contains(user))
            throw new AlreadyAcceptedByUser();
        receivedConfirmations.add(user);
        return isReceived();
    }

    public boolean isReceived() {
        return receivedConfirmations.containsAll(getRequiredConfirmations());
    }

    public Set<RZMUser> getUsersAbleToAccept() {
        return getRequiredConfirmations();
    }

    private Set<RZMUser> getRequiredConfirmations() {
        ObjectFactory of = JbpmContext.getCurrentJbpmContext().getObjectFactory();
        UserManager um = (UserManager) of.createObject("userManager");
        return new HashSet<RZMUser>(um.findUsersInSystemRole(name, type, true, true));
    }
}
