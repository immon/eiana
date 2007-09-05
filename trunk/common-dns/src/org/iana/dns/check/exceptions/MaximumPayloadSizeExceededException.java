package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in MaximumPayloadSizeCheck when response estimated size is greater than 512 byts.
 *
 * @author Piotr Tkaczyk
 */
public class MaximumPayloadSizeExceededException extends DomainTechnicalCheckException {

    private int estimatedSize;

    /**
     * Creates exception from given data.
     *
     * @param domain        current domain
     * @param estimatedSize exceeded estimated response size
     */
    public MaximumPayloadSizeExceededException(DNSDomain domain, int estimatedSize) {
        super(domain, null);
        this.estimatedSize = estimatedSize;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptMaximumPayloadSizeExceededException(this);
    }
}
