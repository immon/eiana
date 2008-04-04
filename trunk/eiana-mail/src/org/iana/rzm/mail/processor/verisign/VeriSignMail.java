package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMail implements MessageData {

    private String domainName;

    public VeriSignMail(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
