package org.iana.rzm.mail.processor.simple.processor;

import org.iana.rzm.mail.processor.simple.data.Message;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EmailProcessor {

    void process(Message msg) throws EmailProcessException;
    
}
