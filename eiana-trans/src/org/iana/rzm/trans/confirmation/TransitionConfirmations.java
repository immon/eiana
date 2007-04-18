package org.iana.rzm.trans.confirmation;

import org.hibernate.annotations.MapKeyManyToMany;
import org.iana.rzm.user.RZMUser;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransitionConfirmations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransitionConfirmations_confirmationSet",
            inverseJoinColumns = @JoinColumn(name = "confirmationSet_objId"))
    @MapKeyManyToMany
    private Map<String, ConfirmationSet> transitionConfirmations = new HashMap<String, ConfirmationSet>();

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public void addConfirmation(String transitionName, Confirmation confirmation) {
        ConfirmationSet cs = transitionConfirmations.get(transitionName);
        if (cs == null) {
            cs = new ConfirmationSet();
            transitionConfirmations.put(transitionName, cs);
        }
        cs.addConfirmation(confirmation);
    }

    public boolean isAcceptableBy(String transitionName, RZMUser user) {
        ConfirmationSet cs = transitionConfirmations.get(transitionName);
        return cs != null && cs.isAcceptableBy(user);
    }
}
