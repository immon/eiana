package org.iana.rzm.facade.admin.trans;

/**
 * @author: Piotr Tkaczyk
 */
public class StateUnreachableException extends Exception {

    private String stateName;

    public StateUnreachableException(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
