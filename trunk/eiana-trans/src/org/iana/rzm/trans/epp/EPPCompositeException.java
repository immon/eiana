package org.iana.rzm.trans.epp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPCompositeException extends EPPException {
    private List<EPPException> exceptions = new ArrayList<EPPException>();

    public List<EPPException> getExceptions() {
        return exceptions;
    }

    public void addException(EPPException exception) {
        exceptions.add(exception);
    }

    public String getMessage() {
        StringBuffer buff = new StringBuffer();
        for (EPPException ex : exceptions)
            buff.append(ex.getMessage()).append("\n");
        return buff.toString();
    }
}
