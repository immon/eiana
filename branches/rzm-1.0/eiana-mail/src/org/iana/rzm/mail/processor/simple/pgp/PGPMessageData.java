package org.iana.rzm.mail.processor.simple.pgp;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface PGPMessageData extends MessageData {

    boolean isPgp();

    void setPgp(boolean pgp);
    
}
