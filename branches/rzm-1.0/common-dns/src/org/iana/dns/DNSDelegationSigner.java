package org.iana.dns;

/**
 * This interface represents a DS record.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSDelegationSigner {

    public int getKeyTag();

    public int getAlg();

    public int getDigestType();

    public String getDigest();

}
