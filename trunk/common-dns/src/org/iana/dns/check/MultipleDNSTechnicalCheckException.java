package org.iana.dns.check;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MultipleDNSTechnicalCheckException extends DNSTechnicalCheckException {

    List<DNSTechnicalCheckException> exceptions = new ArrayList<DNSTechnicalCheckException>();

    public void addException(DNSTechnicalCheckException e) {
        if (e instanceof MultipleDNSTechnicalCheckException) {
            MultipleDNSTechnicalCheckException mex = (MultipleDNSTechnicalCheckException) e;
            exceptions.addAll(mex.getExceptions());
        } else {
            exceptions.add(e);
        }
    }

    public void addExceptions(List<DNSTechnicalCheckException> e) {
        exceptions.addAll(e);
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

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptMultipleDNSTechnicalCheckException(this);
    }
}
