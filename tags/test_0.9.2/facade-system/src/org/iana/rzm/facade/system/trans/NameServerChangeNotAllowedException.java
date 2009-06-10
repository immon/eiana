package org.iana.rzm.facade.system.trans;

/**
 * This exception is thrown when the system policy prohibits a name server change
 * (e.g. when the domain is involved as an impacted party in some other transaction).
 *
 * @author Patrycja Wegrzynowicz
 */
public class NameServerChangeNotAllowedException extends TransactionServiceException {

    public NameServerChangeNotAllowedException() {
    }

    public NameServerChangeNotAllowedException(String message) {
        super(message);
    }

    public NameServerChangeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameServerChangeNotAllowedException(Throwable cause) {
        super(cause);
    }
}
