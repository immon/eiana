package org.iana.rzm.facade.admin.trans;

/**
 * This exception is thrown when the EPP command (like EPP info to get status) is trying to be executed againt
 * a transaction which does not involve EPP i.e. does not contain name server change.
 *
 * @author Patrycja Wegrzynowicz
 */
public class InvalidEPPTransactionException extends Exception {    
}
