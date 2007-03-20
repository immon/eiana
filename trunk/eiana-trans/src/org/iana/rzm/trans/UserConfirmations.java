package org.iana.rzm.trans;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKeyManyToMany;
import org.iana.rzm.user.RZMUser;

import javax.persistence.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class UserConfirmations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @CollectionOfElements
    @JoinTable(name = "Confirmation_Users",
            joinColumns = @JoinColumn(name = "ConfirmationData_objId"))
    @MapKeyManyToMany(joinColumns = @JoinColumn(name = "RZMUser_objId"))
    @Column(name = "confirmed", nullable = false)
    private Map<RZMUser, Boolean> confirmations = new HashMap<RZMUser, Boolean>();


    private UserConfirmations() {
    }

    public UserConfirmations(Set<RZMUser> impactedUsers) {
        for (RZMUser user : impactedUsers)
            confirmations.put(user, false);
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Map<RZMUser, Boolean> getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Map<RZMUser, Boolean> confirmations) {
        this.confirmations = confirmations;
    }
}
