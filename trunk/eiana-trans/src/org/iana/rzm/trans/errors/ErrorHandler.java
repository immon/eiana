package org.iana.rzm.trans.errors;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ErrorHandler {

    public void handleException(Exception e);

    public void handleException(String msg);

}
