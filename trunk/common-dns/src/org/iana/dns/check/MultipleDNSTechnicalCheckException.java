package org.iana.dns.check;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MultipleDNSTechnicalCheckException extends DNSTechnicalCheckException {

    List<DNSTechnicalCheckException> exceptions = new ArrayList<DNSTechnicalCheckException>();

    public void addException(DNSTechnicalCheckException e) {
        exceptions.add(e);
    }

    public void addExceptions(Collection<DNSTechnicalCheckException> e) {
        exceptions.addAll(e);
    }

    public List<DNSTechnicalCheckException> getExceptions() {
        return exceptions;
    }

    public boolean isEmpty() {
        return exceptions.size() == 0;
    }
}
