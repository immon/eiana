package org.iana.rzm.trans;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class StateTransition {

    private String name;

    public StateTransition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
