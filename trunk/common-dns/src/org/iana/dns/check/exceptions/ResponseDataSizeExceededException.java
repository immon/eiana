package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class ResponseDataSizeExceededException extends NameServerTechnicalCheckException {

    private int estimatedSize;

    // no usage for now
    public ResponseDataSizeExceededException(DNSHost host, int estimatedSize) {
        super(host);
        this.estimatedSize = estimatedSize;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }
}
