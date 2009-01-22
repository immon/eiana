package org.iana.dns.obj;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSDelegationSignerImpl {

    private int keyTag;

    private int alg;

    private int digestType;

    private String digest;

    public int getKeyTag() {
        return keyTag;
    }

    public void setKeyTag(int keyTag) {
        this.keyTag = keyTag;
    }

    public int getAlg() {
        return alg;
    }

    public void setAlg(int alg) {
        this.alg = alg;
    }

    public int getDigestType() {
        return digestType;
    }

    public void setDigestType(int digestType) {
        this.digestType = digestType;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
