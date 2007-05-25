package org.iana.notifications;

/**
 * This represents an addressee of a notification. Note that notifications
 * are always send by email thus it an email address needs to be configured properly.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Addressee extends Cloneable {

    Long getObjId();
    
    String getEmail();

    String getName();

    Addressee clone();
}
