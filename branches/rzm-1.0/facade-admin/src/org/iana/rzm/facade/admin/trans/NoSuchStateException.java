package org.iana.rzm.facade.admin.trans;

/**
 * @author: Piotr Tkaczyk
 */
public class NoSuchStateException extends Exception {

    private String stateName;

    public NoSuchStateException(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
    
}
