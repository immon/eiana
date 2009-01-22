package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class StateTransition {

    public static final String ACCEPT = "accept";
    public static final String REJECT = "reject";

    private String name;

    public StateTransition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
