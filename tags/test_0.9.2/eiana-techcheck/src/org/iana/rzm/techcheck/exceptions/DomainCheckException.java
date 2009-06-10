package org.iana.rzm.techcheck.exceptions;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class DomainCheckException extends Exception {

    String domainName;
    Map<String, List<ExceptionMessage>> errors;

    public DomainCheckException(String domainName) {
        this.domainName = domainName;
        errors = new HashMap<String, List<ExceptionMessage>>();
    }

    public void addException(DomainException exception) {
        if(!errors.containsKey(exception.getName())) {
            List<ExceptionMessage> messages = new ArrayList<ExceptionMessage>();
            messages.add(new ExceptionMessage(exception.getOwner(), exception.getValue()));
            errors.put(exception.getName(), messages);
        } else {
            List<ExceptionMessage> messages = errors.get(exception.getName());
            messages.add(new ExceptionMessage(exception.getOwner(), exception.getValue()));
            errors.put(exception.getName(), messages);
        }
    }

    public String getDomainName() {
        return domainName;
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Set<String> getExceptionTypes() {
        return errors.keySet();
    }

    public Set<String> getOwners() {
        Set<String> owners = new HashSet<String>();
        for (String exceptionName : getExceptionTypes()) {
            List<ExceptionMessage> exceptionMessages = getErrorsByExceptionType(exceptionName);
            for (ExceptionMessage exceptionMessage : exceptionMessages)
                owners.add(exceptionMessage.getOwner());
        }
        return owners;
    }

    public Map<String, String> getOwnerExceptions(String ownerName) {
        Map<String, String> exceptions = new HashMap<String, String>();
        for (String exceptioName : getExceptionTypes()) {
            for (ExceptionMessage exceptionMessage : getErrorsByExceptionType(exceptioName))
                if (exceptionMessage.getOwner().equals(ownerName))
                    exceptions.put(exceptioName, exceptionMessage.getValue());
        }
        return exceptions;
    }

    public List<ExceptionMessage> getErrorsByExceptionType(String exceptionName) {
        return (errors.containsKey(exceptionName))? errors.get(exceptionName) : new ArrayList<ExceptionMessage>() ;
    }
}




