package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;

/**
 * @author Piotr Tkaczyk
 */
public class ResponseDataSizeExceededException extends DomainTechnicalCheckException {

    private int estimatedSize;

    /**
     * Thrown in MaximumPayloadSizeCheck when estimated response size exceed 512 bytes;
     *
     * @param domain        - current domain
     * @param estimatedSize - estimated exceeded response size
     */

    public ResponseDataSizeExceededException(DNSDomain domain, int estimatedSize) {
        super(domain, null);
        this.estimatedSize = estimatedSize;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }
}
