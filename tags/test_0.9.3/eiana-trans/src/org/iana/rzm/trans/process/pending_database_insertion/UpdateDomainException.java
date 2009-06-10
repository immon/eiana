/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.process.pending_database_insertion;

public class UpdateDomainException extends Exception {

    public UpdateDomainException (String message) {
        super(message);
    }
}
