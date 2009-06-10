package org.iana.rzm.mail.processor.simple.processor;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IllegalMessageDataException extends EmailProcessException {

    private MessageData data;

    public IllegalMessageDataException(MessageData data) {
        this.data = data;
    }

    public MessageData getData() {
        return data;
    }
}
