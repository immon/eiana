package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class MaximumPayloadExceededException extends NameServerTechnicalCheckException {

    private int estimatedSize;

    /**
     * Thrown in MaximumPayloadSizeCheck when estimated size exceed 512 byts
     *
     * @param host          current host
     * @param estimatedSize exceeded estimated response size
     */
    public MaximumPayloadExceededException(DNSHost host, int estimatedSize) {
        super(host);
        this.estimatedSize = estimatedSize;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }
}
