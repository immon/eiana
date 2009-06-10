package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.pgp.PGPMessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMail implements PGPMessageData {

    private String domainName;

    private boolean pgp;

    public VeriSignMail(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }

    public boolean isPgp() {
        return pgp;
    }

    public void setPgp(boolean pgp) {
        this.pgp = pgp;
    }
}
