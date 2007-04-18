package org.iana.rzm.trans.transitionauth;

import org.hibernate.annotations.MapKeyManyToMany;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class StateTransitionAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "StateTransitionAuth_transitionAuth",
            inverseJoinColumns = @JoinColumn(name = "transitionAuth_objId"))
    @MapKeyManyToMany
    private Map<String, TransitionAuth> transitionAuth = new HashMap<String, TransitionAuth>();

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public void setTransitionAuth(String transitionName, TransitionAuth ta) {
        transitionAuth.put(transitionName, ta);
    }

    public TransitionAuth getTransitionAuth(String transitionName) {
        return transitionAuth.get(transitionName);
    }
}
