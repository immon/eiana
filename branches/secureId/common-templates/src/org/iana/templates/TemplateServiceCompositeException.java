package org.iana.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateServiceCompositeException extends TemplatesServiceException {
    List<TemplatesServiceException> exceptions = new ArrayList<TemplatesServiceException>();

    public TemplateServiceCompositeException(List<TemplatesServiceException> exceptions) {
        super("Composite exception: " + exceptions);
        this.exceptions = exceptions;
    }

    public List<TemplatesServiceException> getExceptions() {
        return exceptions;
    }

    public String[] getMessages() {
        String[] msg = new String[exceptions.size()];
        for (int i = 0; i < msg.length; i++) msg[i] = (exceptions.get(i)).getMessage();
        return msg;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        Iterator<TemplatesServiceException> i = exceptions.iterator();
        if (i.hasNext()) {
            sb.append(i.next().getMessage());
            while (i.hasNext())
                sb.append("\n").append(i.next().getMessage());
        }
        return sb.toString();
    }
}
