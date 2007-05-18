package org.iana.templates.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class ValidationCompositeException extends ValidationException {
    List<ValidationException> exceptions = new ArrayList<ValidationException>();

    public ValidationCompositeException(List<ValidationException> exceptions) {
        super("Composite exception: " + exceptions);
        this.exceptions = exceptions;
    }

    public List<ValidationException> getExceptions() {
        return exceptions;
    }

    public String[] getMessages() {
        String[] msg = new String[exceptions.size()];
        for (int i = 0; i < msg.length; i++) msg[i] = (exceptions.get(i)).getMessage();
        return msg;
    }
}
